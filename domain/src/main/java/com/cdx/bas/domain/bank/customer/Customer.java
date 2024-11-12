package com.cdx.bas.domain.bank.customer;

import com.cdx.bas.domain.bank.account.BankAccount;
import com.cdx.bas.domain.bank.customer.gender.Gender;
import com.cdx.bas.domain.bank.customer.maritalstatus.MaritalStatus;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.*;

public class Customer {

	private static final String ISO_COUNTRY_REGEX = "^(AF|AX|AL|DZ|AS|AD|AO|AI|AQ|AG|AR|AM|AW|AU|AT|AZ|BS|BH|BD|BB|BY|BE|BZ|BJ|BM|BT|BO|BQ|BA|BW|BV|BR|IO|BN|BG|BF|BI|KH|CM|CA|CV|KY|CF|TD|CL|CN|CX|CC|CO|KM|CG|CD|CK|CR|CI|HR|CU|CW|CY|CZ|"
			+ "DK|DJ|DM|DO|EC|EG|SV|GQ|ER|EE|ET|FK|FO|FJ|FI|FR|GF|PF|TF|GA|GM|GE|DE|GH|GI|GR|GL|GD|GP|GU|GT|GG|GN|GW|GY|HT|HM|VA|HN|HK|HU|IS|IN|ID|IR|IQ|IE|IM|IL|IT|JM|JP|JE|JO|KZ|KE|KI|KP|KR|KW|KG|LA|LV|LB|LS|LR|LY|LI|LT|LU|MO|MK|MG|"
			+ "MW|MY|MV|ML|MT|MH|MQ|MR|MU|YT|MX|FM|MD|MC|MN|ME|MS|MA|MZ|MM|NA|NR|NP|NL|NC|NZ|NI|NE|NG|NU|NF|MP|NO|OM|PK|PW|PS|PA|PG|PY|PE|PH|PN|PL|PT|PR|QA|RE|RO|RU|RW|BL|SH|KN|LC|MF|PM|VC|WS|SM|ST|SA|SN|RS|SC|SL|SG|SX|SK|SI|SB|SO|ZA|"
			+ "GS|SS|ES|LK|SD|SR|SJ|SZ|SE|CH|SY|TW|TJ|TZ|TH|TL|TG|TK|TO|TT|TN|TR|TM|TC|TV|UG|UA|AE|GB|US|UM|UY|UZ|VU|VE|VN|VG|VI|WF|EH|YE|ZM|ZW)$";

	@NotNull(message="id must not be null.")
	@Min(value=1, message="id must be positive and greater than 0.")
	private Long id;
	
	@NotNull(message="firstName must not be null.")
	@Size(min = 1, max = 750, message="firstName must contain at least 1 character and must not have more than 750 characters.")
    private String firstName;
	
	@NotNull(message="lastName must not be null.")
	@Size(min = 1, max = 750, message="lastName must contain at least 1 character and must not have more than 750 characters.")
	private String lastName;
	
	@NotNull(message="gender must not be null.")
	private Gender gender;
	
	@NotNull(message="maritalStatus must not be null.")
	private MaritalStatus maritalStatus;
    
	@NotNull(message="birthdate must not be null.")
	@Past(message="birthdate must not be before the current date.")
	private LocalDate birthdate;
    
	@NotNull(message="country must not be null.")
	@Pattern(regexp = ISO_COUNTRY_REGEX, message = "country must contain ISO 3166 country code.")
	private String country;
    
	@NotNull(message="address must not be null.")
	@Size(min = 1, message="address must contain at least 1 character.")
	private String address;
	
	@NotNull(message="city must not be null.")
	@Size(min = 1, message="city must contain at least 1 character.")
	private String city;

	@NotNull(message="email must not be null.")
	@Email(message = "email must respect the email format.")
	@Size(min = 1, message="address must contain at least 1 character.")
	private String email;
	
	@NotNull(message="phoneNumber must not be null.")
	@Size(min = 5, max = 20, message="phoneNumber must contain at least 5 digits and maximum 20 digits.")
	private String phoneNumber;
	
	@NotNull(message="accounts must not be null.")
	private List<BankAccount> accounts = new ArrayList<>();
	
	private Map<String, String> metadata = new HashMap<>();

	public Customer() {
	}

	public Customer(Long id, String firstName, String lastName, Gender gender, MaritalStatus maritalStatus, LocalDate birthdate, String country, String address, String city, String email, String phoneNumber, List<BankAccount> accounts, Map<String, String> metadata) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.gender = gender;
		this.maritalStatus = maritalStatus;
		this.birthdate = birthdate;
		this.country = country;
		this.address = address;
		this.city = city;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.accounts = accounts;
		this.metadata = metadata;
	}

	public @NotNull(message = "id must not be null.") @Min(value = 1, message = "id must be positive and greater than 0.") Long getId() {
		return id;
	}

	public void setId(@NotNull(message = "id must not be null.") @Min(value = 1, message = "id must be positive and greater than 0.") Long id) {
		this.id = id;
	}

	public @NotNull(message = "firstName must not be null.") @Size(min = 1, max = 750, message = "firstName must contain at least 1 character and must not have more than 750 characters.") String getFirstName() {
		return firstName;
	}

	public void setFirstName(@NotNull(message = "firstName must not be null.") @Size(min = 1, max = 750, message = "firstName must contain at least 1 character and must not have more than 750 characters.") String firstName) {
		this.firstName = firstName;
	}

	public @NotNull(message = "lastName must not be null.") @Size(min = 1, max = 750, message = "lastName must contain at least 1 character and must not have more than 750 characters.") String getLastName() {
		return lastName;
	}

	public void setLastName(@NotNull(message = "lastName must not be null.") @Size(min = 1, max = 750, message = "lastName must contain at least 1 character and must not have more than 750 characters.") String lastName) {
		this.lastName = lastName;
	}

	public @NotNull(message = "gender must not be null.") Gender getGender() {
		return gender;
	}

	public void setGender(@NotNull(message = "gender must not be null.") Gender gender) {
		this.gender = gender;
	}

	public @NotNull(message = "maritalStatus must not be null.") MaritalStatus getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(@NotNull(message = "maritalStatus must not be null.") MaritalStatus maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public @NotNull(message = "birthdate must not be null.") @Past(message = "birthdate must not be before the current date.") LocalDate getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(@NotNull(message = "birthdate must not be null.") @Past(message = "birthdate must not be before the current date.") LocalDate birthdate) {
		this.birthdate = birthdate;
	}

	public @NotNull(message = "country must not be null.") @Pattern(regexp = ISO_COUNTRY_REGEX, message = "country must contain ISO 3166 country code.") String getCountry() {
		return country;
	}

	public void setCountry(@NotNull(message = "country must not be null.") @Pattern(regexp = ISO_COUNTRY_REGEX, message = "country must contain ISO 3166 country code.") String country) {
		this.country = country;
	}

	public @NotNull(message = "address must not be null.") @Size(min = 1, message = "address must contain at least 1 character.") String getAddress() {
		return address;
	}

	public void setAddress(@NotNull(message = "address must not be null.") @Size(min = 1, message = "address must contain at least 1 character.") String address) {
		this.address = address;
	}

	public @NotNull(message = "city must not be null.") @Size(min = 1, message = "city must contain at least 1 character.") String getCity() {
		return city;
	}

	public void setCity(@NotNull(message = "city must not be null.") @Size(min = 1, message = "city must contain at least 1 character.") String city) {
		this.city = city;
	}

	public @NotNull(message = "email must not be null.") @Email(message = "email must respect the email format.") @Size(min = 1, message = "address must contain at least 1 character.") String getEmail() {
		return email;
	}

	public void setEmail(@NotNull(message = "email must not be null.") @Email(message = "email must respect the email format.") @Size(min = 1, message = "address must contain at least 1 character.") String email) {
		this.email = email;
	}

	public @NotNull(message = "phoneNumber must not be null.") @Size(min = 5, max = 20, message = "phoneNumber must contain at least 5 digits and maximum 20 digits.") String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(@NotNull(message = "phoneNumber must not be null.") @Size(min = 5, max = 20, message = "phoneNumber must contain at least 5 digits and maximum 20 digits.") String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public List<BankAccount> getAccounts() {
		return accounts;
	}

	public void setAccounts(@NotNull(message = "accounts must not be null.") List<BankAccount> accounts) {
		this.accounts = accounts;
	}

	public Map<String, String> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Customer customer = (Customer) o;
		return Objects.equals(id, customer.id) && Objects.equals(firstName, customer.firstName) && Objects.equals(lastName, customer.lastName) && gender == customer.gender && maritalStatus == customer.maritalStatus && Objects.equals(birthdate, customer.birthdate) && Objects.equals(country, customer.country) && Objects.equals(address, customer.address) && Objects.equals(city, customer.city) && Objects.equals(email, customer.email) && Objects.equals(phoneNumber, customer.phoneNumber) && Objects.equals(accounts, customer.accounts) && Objects.equals(metadata, customer.metadata);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, firstName, lastName, gender, maritalStatus, birthdate, country, address, city, email, phoneNumber, accounts, metadata);
	}
}
