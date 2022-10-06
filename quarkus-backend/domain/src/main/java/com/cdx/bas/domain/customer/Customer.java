package com.cdx.bas.domain.customer;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.cdx.bas.domain.bank.account.BankAccount;

public class Customer {

	private static final String ISO_COUNTRY_REGEX = "^(AF|AX|AL|DZ|AS|AD|AO|AI|AQ|AG|AR|AM|AW|AU|AT|AZ|BS|BH|BD|BB|BY|BE|BZ|BJ|BM|BT|BO|BQ|BA|BW|BV|BR|IO|BN|BG|BF|BI|KH|CM|CA|CV|KY|CF|TD|CL|CN|CX|CC|CO|KM|CG|CD|CK|CR|CI|HR|CU|CW|CY|CZ|"
			+ "DK|DJ|DM|DO|EC|EG|SV|GQ|ER|EE|ET|FK|FO|FJ|FI|FR|GF|PF|TF|GA|GM|GE|DE|GH|GI|GR|GL|GD|GP|GU|GT|GG|GN|GW|GY|HT|HM|VA|HN|HK|HU|IS|IN|ID|IR|IQ|IE|IM|IL|IT|JM|JP|JE|JO|KZ|KE|KI|KP|KR|KW|KG|LA|LV|LB|LS|LR|LY|LI|LT|LU|MO|MK|MG|"
			+ "MW|MY|MV|ML|MT|MH|MQ|MR|MU|YT|MX|FM|MD|MC|MN|ME|MS|MA|MZ|MM|NA|NR|NP|NL|NC|NZ|NI|NE|NG|NU|NF|MP|NO|OM|PK|PW|PS|PA|PG|PY|PE|PH|PN|PL|PT|PR|QA|RE|RO|RU|RW|BL|SH|KN|LC|MF|PM|VC|WS|SM|ST|SA|SN|RS|SC|SL|SG|SX|SK|SI|SB|SO|ZA|"
			+ "GS|SS|ES|LK|SD|SR|SJ|SZ|SE|CH|SY|TW|TJ|TZ|TH|TL|TG|TK|TO|TT|TN|TR|TM|TC|TV|UG|UA|AE|GB|US|UM|UY|UZ|VU|VE|VN|VG|VI|WF|EH|YE|ZM|ZW)$";

	@Min(value=1, message="id must be positive and greater than 0.")
	private long id;
	
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
	private LocalDateTime birthdate;
    
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
	private Set<BankAccount> accounts = new HashSet<>();
	
	private Map<String, String> metadatas = new HashMap<>();
	
	public Customer() {
		super();
	}

	public Customer(Long id, String firstName, String lastName, Gender gender, MaritalStatus maritalStatus,
	        LocalDateTime birthdate, String country, String address, String city, String email, String phoneNumber,
	        Set<BankAccount> accounts, Map<String, String> metadatas) {
		super();
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
		this.metadatas = metadatas;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
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
	
	public Gender getGender() {
		return gender;
	}
	
	public void setGender(Gender gender) {
		this.gender = gender;
	}
	
	public MaritalStatus getMaritalStatus() {
		return maritalStatus;
	}
	
	public void setMaritalStatus(MaritalStatus maritalStatus) {
		this.maritalStatus = maritalStatus;
	}
	
	public LocalDateTime getBirthdate() {
		return birthdate;
	}
	
	public void setBirthdate(LocalDateTime birthdate) {
		this.birthdate = birthdate;
	}
	
	public String getCountry() {
		return country;
	}
	
	public void setCountry(String country) {
		this.country = country;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public Set<BankAccount> getAccounts() {
		return accounts;
	}
	
	public void setAccounts(Set<BankAccount> accounts) {
		this.accounts = accounts;
	}
	
	public Map<String, String> getMetadatas() {
		return metadatas;
	}

	public void setMetadatas(Map<String, String> metadatas) {
		this.metadatas = metadatas;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(accounts, address, birthdate, city, email, firstName, gender, id, lastName, maritalStatus,
				metadatas, country, phoneNumber);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
		    return true;
		}
		if (obj == null)  {
		    return false;
		}
		if (getClass() != obj.getClass()) {
		    return false;
		}
		Customer other = (Customer) obj;
		return Objects.equals(accounts, other.accounts) && Objects.equals(address, other.address)
				&& Objects.equals(birthdate, other.birthdate) && Objects.equals(city, other.city)
				&& Objects.equals(email, other.email) && Objects.equals(firstName, other.firstName)
				&& gender == other.gender && id == other.id && Objects.equals(lastName, other.lastName)
				&& maritalStatus == other.maritalStatus && Objects.equals(metadatas, other.metadatas)
				&& Objects.equals(country, other.country) && Objects.equals(phoneNumber, other.phoneNumber);
	}
}
