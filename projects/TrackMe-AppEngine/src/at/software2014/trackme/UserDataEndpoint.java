package at.software2014.trackme;

import at.software2014.trackme.EMF;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.datanucleus.query.JPACursorHelper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

@Api(name = "userdataendpoint", namespace = @ApiNamespace(ownerDomain = "software2014.at", ownerName = "software2014.at", packagePath = "trackme"))
public class UserDataEndpoint {

	/**
	 * This method lists all the entities inserted in datastore.
	 * It uses HTTP GET method and paging support.
	 *
	 * @return A CollectionResponse class containing the list of all entities
	 * persisted and a cursor to the next page.
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

			// Tight loop for fetching all entities from datastore and accomodate
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
	 * This method gets the entity having primary key id. It uses HTTP GET method.
	 *
	 * @param id the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	@ApiMethod(name = "getUserData")
	public UserData getUserData(@Named("id") String id) {
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
	 * This inserts a new entity into App Engine datastore. If the entity already
	 * exists in the datastore, an exception is thrown.
	 * It uses HTTP POST method.
	 *
	 * @param userdata the entity to be inserted.
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
	 * This method is used for updating an existing entity. If the entity does not
	 * exist in the datastore, an exception is thrown.
	 * It uses HTTP PUT method.
	 *
	 * @param userdata the entity to be updated.
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
	 * This method removes the entity with primary key id.
	 * It uses HTTP DELETE method.
	 *
	 * @param id the primary key of the entity to be deleted.
	 */
	@ApiMethod(name = "removeUserData")
	public void removeUserData(@Named("id") String id) {
		EntityManager mgr = getEntityManager();
		try {
			UserData userdata = mgr.find(UserData.class, id);
			mgr.remove(userdata);
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
	public void register(@Named("email") String email, @Named("name") String name) {
			
		UserData ud = getUserData(email); 
		if(ud == null)
		{
			ud = new UserData();
			ud.setUserEmail(email);
			ud.setUserName(name);
			insertUserData(ud); 
			System.out.println("User " + email + " " + name + " was registered successfully."); 
		}
		else
		{
			System.out.println("User " + email + " is already registered."); 
		}
	}
	
	@ApiMethod(name = "updateLocation")
	public void updateLocation(@Named("email")String email, @Named("latitude") double latitude, @Named("longitude") double longitude, @Named("timestamp") long timestamp)
	{
		EntityManager mgr = getEntityManager();
		UserData ud = mgr.find(UserData.class, email);
		if(ud == null)
		{
			System.out.println("User " + email + " does not exist."); 
		}
		else
		{
			ud.setUserLastLatitude(latitude);
			ud.setUserLastLongitude(longitude);
			ud.setTimestamp(timestamp);
			mgr.merge(ud); 
			System.out.println("User " + email + " got his/her location updated."); 
		}
		
		mgr.close(); 
	}
	
	@ApiMethod(name = "addAllowedUser")
	public void addAllowedUser(@Named("ownEmail") String ownEmail, @Named("userEmail") String userEmail) {
		
		EntityManager mgr = getEntityManager();
		UserData ownUd = mgr.find(UserData.class, ownEmail);
		
		List<String> allowedUsers = ownUd.getAllowedUsersForQuerying();
		
		if(allowedUsers == null) {
			allowedUsers = new ArrayList<String>();
		}
		allowedUsers.add(userEmail);
		
		ownUd.setAllowedUsersForQuerying(allowedUsers);
		
		mgr.merge(ownUd);
		mgr.close();
	}
	
	
	@ApiMethod(name = "getUserDataList")
	public List<UserData> getUserDataList(@Named("emailList") List<String> emailList) {
		
		EntityManager mgr = getEntityManager();
		List<UserData> userDataList = new ArrayList<UserData>();
		
		for(String email : emailList) {
			
			UserData ud = mgr.find(UserData.class, email);
			userDataList.add(ud);
		}
		
		mgr.close();
		return userDataList;
		
	}
	

	private static EntityManager getEntityManager() {
		return EMF.get().createEntityManager();
	}

}
