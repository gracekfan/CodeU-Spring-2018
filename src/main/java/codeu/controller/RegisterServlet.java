package codeu.controller;

import codeu.model.data.User;
import codeu.model.store.basic.UserStore;
import codeu.model.store.basic.AdminStore;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.mindrot.jbcrypt.BCrypt;

/**
* Servlet class responsible for user registration.
*/
public class RegisterServlet extends HttpServlet {

	/**
	* Store class that gives access to Users.
	*/
	private UserStore userStore;

	private AdminStore adminStore;
		 
	/**
	* Set up state for handling registration-related requests. This method is only called when
	* running in a server, not when running in a test.
	*/
	@Override
	public void init() throws ServletException {
		super.init();
		setUserStore(UserStore.getInstance());
		setAdminStore(AdminStore.getInstance());
	}
		 
		 /**
		  * Sets the UserStore used by this servlet. This function provides a common setup method
		  * for use by the test framework or the servlet's init() function.
		  */
	void setUserStore(UserStore userStore) {
		this.userStore = userStore;
	}

	void setAdminStore(AdminStore adminStore){
		this.adminStore = adminStore;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	    throws IOException, ServletException {

   		request.getRequestDispatcher("/WEB-INF/view/register.jsp").forward(request, response);
   	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
    	throws IOException, ServletException {

		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String passwordHash = BCrypt.hashpw(password,BCrypt.gensalt());
            
		if (!username.matches("[\\w*\\s*]*")) {
		    request.setAttribute("error", "Please enter only letters, numbers, and spaces.");
		    request.getRequestDispatcher("/WEB-INF/view/register.jsp").forward(request, response);
		    return;
   		}

   		if (userStore.isUserRegistered(username)) {
    		request.setAttribute("error", "That username is already taken.");
    		request.getRequestDispatcher("/WEB-INF/view/register.jsp").forward(request, response);
    		return;
    	}



    	User user = new User(UUID.randomUUID(), username, passwordHash, Instant.now());
        user.setAdmin(false);
   		userStore.addUser(user);
   		adminStore.addUser(user);

   		if(adminStore.getTotalUsers() == 1){
   			user.setAdmin(true);
   			userStore.updateUser(user);
   		}


   		response.sendRedirect("/login");
 	}


}
