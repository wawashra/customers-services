package io.wawashra.customers.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.vertx.core.json.JsonObject;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreatedCustomer {

    private String id;
    private String firstName;
    private String lastName;
    private int age;
    private Date createAt;


    public CreatedCustomer() {
    }

    public CreatedCustomer(JsonObject jsonObject) {
        this.id = jsonObject.getString("id");
        this.firstName = jsonObject.getString("firstName");
        this.lastName = jsonObject.getString("lastName");
        this.age = jsonObject.getInteger("age");
        this.createAt = new Date(jsonObject.getLong("createAt"));
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}
}
