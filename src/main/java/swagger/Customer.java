package swagger;

import jakarta.persistence.*;

@Entity
@Table(name = "customers")
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "custom_id")
	private String customId;

	@Deprecated // only for hibernate
	public Customer() {
	}

	public Customer(String firstName, String lastName, String customId) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.customId = customId;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getCustomId() {
		return customId;
	}

	@Override
	public String toString() {
		return "Customer[" +
				"id=" + id +
				", firstName='" + firstName + '\'' +
				", lastName='" + lastName + '\'' +
				", customId='" + customId + '\'' +
				']';
	}
}
