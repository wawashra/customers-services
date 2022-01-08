package io.wawashra.customers.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.vertx.core.json.JsonObject;
import io.wawashra.customers.utils.DateUtils;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeletedCustomerDTO {

    private String firstName;
    private String lastName;
    private int age;
    private String deleteAt;


    public DeletedCustomerDTO() {
    }

    public DeletedCustomerDTO(JsonObject jsonObject) {
        this.firstName = jsonObject.getString("firstName");
        this.lastName = jsonObject.getString("lastName");
        this.age = jsonObject.getInteger("age");
        this.deleteAt = DateUtils.getDeletedDateFormated(jsonObject.getLong("deleteAt"));
    }

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getDeleteAt() {
		return deleteAt;
	}

	public void setDeleteAt(String deleteAt) {
		this.deleteAt = deleteAt;
	}
}
