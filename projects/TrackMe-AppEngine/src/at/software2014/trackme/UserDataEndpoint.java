package at.software2014.trackme;

import at.software2014.trackme.EMF;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.datanucleus.query.JPACursorHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.validator.routines.EmailValidator;

@Api(name = "userdataendpoint", namespace = @ApiNamespace(ownerDomain = "software2014.at", ownerName = "software2014.at", packagePath = "trackme"))
public class UserDataEndpoint {

	/**
	 * This method lists all the entities inserted in datastore. It uses HTTP
	 * GET method and paging support.
	 * 
	 * @return A CollectionResponse class containing the list of all entities
	 *         persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listUserData")
	public CollectionResponse<UserData> listUserData(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		EntityManager mgr = null;
		Cursor cursor = null;
		List<UserData> execute = null;

		try {
			mgr = getEntityManager();
			Query query = mgr.createQuery("select from UserData as UserData");
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				query.setHint(JPACursorHelper.CURSOR_HINT, cursor);
			}

			if (limit != null) {
				query.setFirstResult(0);
				query.setMaxResults(limit);
			}

			execute = (List<UserData>) query.getResultList();
			cursor = JPACursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and
			// accomodate
			// for lazy fetch.
			for (UserData obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<UserData> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET
	 * method.
	 * 
	 * @param id
	 *            the primary key of the java bean.
	 * @return The entity with primary key id.
	 * @throws BadRequestException
	 */
	@ApiMethod(name = "getUserData")
	public UserData getUserData(@Named("id") String id)
			throws BadRequestException {

		validateEmailAddress(id);

		EntityManager mgr = getEntityManager();
		UserData userdata = null;
		try {
			userdata = mgr.find(UserData.class, id);
		} finally {
			mgr.close();
		}
		return userdata;
		
		
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity
	 * already exists in the datastore, an exception is thrown. It uses HTTP
	 * POST method.
	 * 
	 * @param userdata
	 *            the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertUserData")
	public UserData insertUserData(UserData userdata) {
		EntityManager mgr = getEntityManager();
		try {
			if (containsUserData(userdata)) {
				throw new EntityExistsException("Object already exists");
			}
			mgr.persist(userdata);
		} finally {
			mgr.close();
		}
		return userdata;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does
	 * not exist in the datastore, an exception is thrown. It uses HTTP PUT
	 * method.
	 * 
	 * @param userdata
	 *            the entity to be updated.
	 * @return The updated entity.
	 */
	@ApiMethod(name = "updateUserData")
	public UserData updateUserData(UserData userdata) {
		EntityManager mgr = getEntityManager();
		try {
			if (!containsUserData(userdata)) {
				throw new EntityNotFoundException("Object does not exist");
			}
			mgr.persist(userdata);
		} finally {
			mgr.close();
		}
		return userdata;
	}

	/**
	 * This method removes the entity with primary key id. It uses HTTP DELETE
	 * method.
	 * 
	 * @param id
	 *            the primary key of the entity to be deleted.
	 * @throws BadRequestException 
	 */
	@ApiMethod(name = "removeUserData")
	public void removeUserData(@Named("id") String id) throws BadRequestException {
		
		validateEmailAddress(id);
		
		EntityManager mgr = getEntityManager();
		try {
			UserData userdata = mgr.find(UserData.class, id);
			mgr.remove(userdata);
			
			List<UserData> otherUsers = getRegisteredUsers(); 
			
			for(UserData otherUser : otherUsers) {
				for(String email : otherUser.getAllowedUsersForQuerying()) {
					
					if(email.equals(userdata.getUserEmail())) {
						
						removeAllowedUser(otherUser.getUserEmail(),email);
					}
				}
			}
			
		} finally {
			mgr.close();
		}
	}

	private boolean containsUserData(UserData userdata) {
		EntityManager mgr = getEntityManager();
		boolean contains = true;
		try {
			UserData item = mgr.find(UserData.class, userdata.getUserEmail());
			if (item == null) {
				contains = false;
			}
		} finally {
			mgr.close();
		}
		return contains;
	}

	@ApiMethod(name = "register")
	public void register(@Named("email") String email,
			@Named("name") String name) throws BadRequestException {
		
		validateEmailAddress(email);

		UserData ud = getUserData(email);
		if (ud == null) {

			ud = new UserData();
			ud.setUserEmail(email);
			ud.setUserName(name);
			insertUserData(ud);
			System.out.println("User " + email + " " + name
					+ " was registered successfully.");
		} else {
			System.out.println("User " + email + " is already registered.");
		}
	}

	@ApiMethod(name = "updateLocation")
	public void updateLocation(@Named("email") String email,
			@Named("latitude") double latitude,
			@Named("longitude") double longitude,
			@Named("timestamp") long timestamp) throws BadRequestException {
		
		validateEmailAddress(email);
		
		EntityManager mgr = getEntityManager();
		UserData ud = mgr.find(UserData.class, email);
		if (ud == null) {
			System.out.println("User " + email + " does not exist.");
		} else {
			ud.setUserLastLatitude(latitude);
			ud.setUserLastLongitude(longitude);
			ud.setTimestamp(timestamp);
			mgr.merge(ud);
			System.out.println("User " + email
					+ " got his/her location updated.");
		}

		mgr.close();
	}

	@ApiMethod(name = "addAllowedUser")
	public void addAllowedUser(@Named("ownEmail") String ownEmail,
			@Named("userEmail") String userEmail) throws BadRequestException {
		
		validateEmailAddress(ownEmail);

		EntityManager mgr = getEntityManager();
		UserData ownUd = mgr.find(UserData.class, ownEmail);

		List<String> allowedUsers = ownUd.getAllowedUsersForQuerying();

		if (allowedUsers == null) {
			allowedUsers = new ArrayList<String>();
		}

		if (!allowedUsers.contains(userEmail)) {
			allowedUsers.add(userEmail);
			ownUd.setAllowedUsersForQuerying(allowedUsers);
			mgr.merge(ownUd);
		}
		mgr.close();
	}

	@ApiMethod(name = "removeAllowedUser")
	public void removeAllowedUser(@Named("ownEmail") String ownEmail,
			@Named("userEmail") String userEmail) throws BadRequestException {
		
		validateEmailAddress(ownEmail); 

		EntityManager mgr = getEntityManager();
		UserData ownUd = mgr.find(UserData.class, ownEmail);

		List<String> allowedUsers = ownUd.getAllowedUsersForQuerying();

		if (allowedUsers.contains(userEmail)) {
			allowedUsers.remove(userEmail);
			ownUd.setAllowedUsersForQuerying(allowedUsers);
			mgr.merge(ownUd);
		}

		mgr.close();
	}

	@ApiMethod(name = "getAllowedUsers")
	public List<UserData> getAllowedUsers(@Named("ownEmail") String ownEmail) throws BadRequestException {
		
		validateEmailAddress(ownEmail);

		EntityManager mgr = getEntityManager();
		UserData ownUd = mgr.find(UserData.class, ownEmail);

		List<UserData> allowedUsers = new ArrayList<UserData>();
		for (String allowedUserIds : ownUd.getAllowedUsersForQuerying()) {
			allowedUsers.add(mgr.find(UserData.class, allowedUserIds));
		}

		mgr.close();
		return allowedUsers;
	}

	@ApiMethod(name = "getRegisteredUsers")
	public List<UserData> getRegisteredUsers() {
		
		EntityManager mgr = getEntityManager();
		Query query = mgr.createQuery("select from UserData as UserData");
		List<UserData> users = (List<UserData>) query.getResultList();
		return users;
	}

	private static EntityManager getEntityManager() {
		return EMF.get().createEntityManager();
	}

	
	//private static final Pattern rfc2822 = Pattern.compile("^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$");
//	if (!rfc2822.matcher(email).matches()) {
//	    throw new BadRequestException(email + " is not a valid address."); 
//	}
	
	private static void validateEmailAddress(String email)
			throws BadRequestException {
		
		if( email == null || email.length() == 0 || !EmailValidator.getInstance().isValid(email))
		{
			 throw new BadRequestException(email + " is not a valid email address."); 
		}
		
	
	}

}
