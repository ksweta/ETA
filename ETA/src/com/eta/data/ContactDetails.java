package com.eta.data;

import java.util.Date;

public class ContactDetails {
   //This id will be provided by the SQLite.
   //It is auto-increment.
   private Long id;
   
   //Recipient name.
   private String name;
   
   //Recipient Phone number.
   private String phone;
   
   //'true' if the phone number is registered with ETA
   // Otherwise 'false'
   private Boolean registered;
   
   //When this contact was last synced with ETA server.
   private Date syncDate;

   public ContactDetails() {
      //Required by the system.
   }
   public ContactDetails(String name, 
         String phone,
         Boolean registered, 
         Date syncDate) {
      //Passing ID as zero, this value will be ignored later.
      this(0L, name, phone, registered, syncDate);
   }
   
   public ContactDetails(Long id, 
                         String name, 
                         String phone,
                         Boolean registered, 
                         Date syncDate) {
      super();
      this.id = id;
      this.name = name;
      this.phone = phone;
      this.registered = registered;
      this.syncDate = syncDate;
   }

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getPhone() {
      return phone;
   }

   public void setPhone(String phone) {
      this.phone = phone;
   }

   public Boolean getRegistered() {
      return registered;
   }

   public void setRegistered(Boolean registered) {
      this.registered = registered;
   }

   public boolean isRegistered() {
      return this.registered;
   }
   public Date getSyncDate() {
      return syncDate;
   }

   public void setSyncDate(Date syncDate) {
      this.syncDate = syncDate;
   }
   
   
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((phone == null) ? 0 : phone.hashCode());
      return result;
   }
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      ContactDetails other = (ContactDetails) obj;
      if (phone == null) {
         if (other.phone != null)
            return false;
      } else if (!phone.equals(other.phone))
         return false;
      return true;
   }
   @Override
   public String toString() {
      return "ContactDetails [id=" + id + ", name=" + name + ", phone="
            + phone + ", registered=" + registered + ", syncDate="
            + syncDate + "]";
   }
}
