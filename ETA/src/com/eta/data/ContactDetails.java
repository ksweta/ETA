package com.eta.data;

public class ContactDetails {
	private Long id;
	private String name;
	private String phone;
	//'true' if the phone number is registered with ETA
	// Otherwise 'false'
	private Boolean registered;

	public ContactDetails(){
		//For system.
	}

	public ContactDetails(String name, String phone, Boolean registered) {
		this(0L, name, phone, registered);
	}
	
	public ContactDetails(Long id, String name, String phone, Boolean registered) {
		super();
		this.id = id;
		this.name = name;
		this.phone = phone;
		this.registered = registered;
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

	@Override
	public String toString() {
		return "ContactDetails [id=" + id + ", name=" + name + ", phone="
				+ phone + ", registered=" + registered + "]";
	}
}
