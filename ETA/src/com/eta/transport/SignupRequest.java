package com.eta.transport;
/**
 *  Server needs the following json format in RegistrationRequest.
 * {"user": {"firstName":"kumari",
 *  "lastName":"sweta",
 *  "email":"ksweta@mail.sfsu.edu",
 *  "phoneNumber":"5105555555",
 *  "password":"madam0",
 *  "clientRegistrationId": "Abcd"}
 * }
 *
 */
public class SignupRequest {

   protected User user;

   public SignupRequest(User user) {
      this.user = user;
   }

   public SignupRequest() {
      // it is used by Gson
   }

   public User getUser() {
      return user;
   }

   public void setUser(User user) {
      this.user = user;
   }

   @Override
   public String toString() {
      return "RegistrationRequest [user=" + user + "]";
   }
}
