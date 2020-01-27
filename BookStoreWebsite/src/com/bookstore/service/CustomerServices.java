package com.bookstore.service;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bookstore.dao.CustomerDAO;
import com.bookstore.entity.Customer;

public class CustomerServices {

		
	private CustomerDAO customerDAO;
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	
	public CustomerServices(HttpServletRequest request, HttpServletResponse response) {
		super();
		this.request = request;
		this.response = response;
		customerDAO = new CustomerDAO();
	}
	
	
	
	public void listCustomers(String message) throws ServletException, IOException {
		
		if(message != null) {
			request.setAttribute("message", message);
		}
		
		 customerDAO = new CustomerDAO();
		
		List<Customer> listCustomer = customerDAO.listAll();
		
		
		
		request.setAttribute("listCustomer", listCustomer);
		
		String listPage = "customer_list.jsp";
		
		
		RequestDispatcher requestDispatcher = request.getRequestDispatcher(listPage);
		
		requestDispatcher.forward(request, response);
		
	}
	
	public void listCustomers() throws ServletException, IOException {
		
		listCustomers(null);
	}
	
	
	
	public void createCustomer() throws ServletException, IOException {
		
		String email = request.getParameter("email");
		
		
		Customer existCustomer = customerDAO.findByEmail(email);
		
		if(existCustomer != null) {
			
			String message = "Could not create customer the email : " + email + " is already register by customer";
			
			listCustomers(message);
			
		}else {
			
			Customer newCustomer = new Customer();
			updateCustomerFieldsFromForm(newCustomer);
			customerDAO.create(newCustomer);
			
			String message = "new customer has been created successsfully";
			listCustomers(message);
			
			
		}
		
		
	}
	
	private void updateCustomerFieldsFromForm(Customer customer) {
		
		String email = request.getParameter("email");
		String fullName = request.getParameter("fullName");
		String password = request.getParameter("password");
		String phone = request.getParameter("phone");
		String address = request.getParameter("address");
		String city = request.getParameter("city");
		String zipCode = request.getParameter("zipcode");
		String country = request.getParameter("country");
		
		if(email != null &&  !email.equals("")) {
			customer.setEmail(email);
		}
		
		customer.setFullname(fullName);
		
		if(password != null &&  !password.equals("")) {
			customer.setPassword(password);
		}
		
		customer.setPhone(phone);
		customer.setAddress(address);
		customer.setCity(city);
		customer.setZipcode(zipCode);
		customer.setCountry(country);		
	}
	

	public void editCustomer() throws ServletException, IOException {
	
		Integer customerId  = Integer.parseInt(request.getParameter("id"));
		
		Customer customer  = customerDAO.get(customerId);
		
		request.setAttribute("customer", customer);
		
		String editPage = "customer_form.jsp";
		
		RequestDispatcher requestDispatcher = request.getRequestDispatcher(editPage);
		
		requestDispatcher.forward(request, response);
		
	}

	public void updateCustomer() throws ServletException, IOException {
		

		Integer customerId  = Integer.parseInt(request.getParameter("customerId"));
		
		String customerByEmail = request.getParameter("email");
		
		Customer existCustomer  = customerDAO.findByEmail(customerByEmail);
		
		String message = null;
		
		if(existCustomer != null && existCustomer.getCustomerId() != customerId) {
			
			 message= "Could not uodate the customer ID " + customerId + "because there is  an existing customer having the same email";
			
			
		}else {
			
			
			Customer customerById = customerDAO.get(customerId);
			updateCustomerFieldsFromForm(customerById);
			
			customerDAO.update(customerById);
			
			message = "The customer has been updated successfully.";
			
			
		}
		
		listCustomers(message);
		
		
	}

	public void deleteCustomer() throws ServletException, IOException {
		
		Integer customerId  = Integer.parseInt(request.getParameter("id"));
		
		customerDAO.delete(customerId);
		
		String message ="The customer has been deleted successfully";
		
		listCustomers(message);
		
	}
	
	public void registerCustomer() throws ServletException, IOException {
		
		String email = request.getParameter("email");
		
		String message = "";
		
		Customer existCustomer = customerDAO.findByEmail(email);
		
		if(existCustomer != null) {
			
			 message = "Could not register. The email : " + email + " is already register by another customer";
			
			
			
		}else {
			

			Customer newCustomer = new Customer();
			updateCustomerFieldsFromForm(newCustomer);			
			customerDAO.create(newCustomer);
			
			message = "You have registered successfully! Thank you.<br/>"
					+ "<a href='login'>Click here</a> to login";			
			
		}
		
		String messagePage = "frontend/message.jsp";
		
		RequestDispatcher requestDispatcher = request.getRequestDispatcher(messagePage);
		
		request.setAttribute("message", message);
		
		requestDispatcher.forward(request, response);
		
	}



	public void showLogin() throws ServletException, IOException {
		
		String loginPage = "frontend/login.jsp";
		RequestDispatcher dispatcher = request.getRequestDispatcher(loginPage);
		dispatcher.forward(request, response);
		
	}



	public void doLogin() throws ServletException, IOException {
	
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		
		Customer customer = customerDAO.checkLogin(email, password);
		
		if(customer == null) {
			String message ="login Failed. Please Check Email and Password";
			
			request.setAttribute("message", message);
			showLogin();
			
			
			
		}else {
			
			HttpSession session = request.getSession();
			session.setAttribute("loggedCustomer", customer);
			
			Object objectRedirectURL = session.getAttribute("redirectURL");
			
			if(objectRedirectURL != null) {
				
				String redirectURL = (String) objectRedirectURL;
				
				session.removeAttribute("redirectURL");
				response.sendRedirect(redirectURL);
				
			}else {
				
				showCustomerProfile();
			}
			
			
			
			
		}
	}
	
	public void showCustomerProfile() throws ServletException, IOException {
		
		String profilePage = "frontend/customer_profile.jsp";
		RequestDispatcher dispatcher = request.getRequestDispatcher(profilePage);
		dispatcher.forward(request, response);
		
	}



	public void showCustomerProfileEditForm() throws ServletException, IOException {
		

		String editPage = "frontend/edit_profile.jsp";
		RequestDispatcher dispatcher = request.getRequestDispatcher(editPage);
		dispatcher.forward(request, response);
		
	}



	public void updateCustomerProfile() throws ServletException, IOException {
	
		Customer customer = (Customer) request.getSession().getAttribute("loggedCustomer");
		
		updateCustomerFieldsFromForm(customer);
		
		customerDAO.update(customer);
		
		showCustomerProfile();
		
		
		
	}
	
}
