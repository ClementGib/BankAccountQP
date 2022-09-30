package com.cdx.bas.domain.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.HashSet;

import com.cdx.bas.domain.bank.account.BankAccount;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class CustomerTest {

	@Test
	public void validate_should_validateCustomerObject_when_fillAllFieldsWithValidValues() {
		HashSet<BankAccount> accounts = new HashSet<BankAccount>();
		accounts.add(new BankAccount());
		HashMap<String, String> metadatas = new HashMap<String, String>();
		metadatas.put("contact_preferences", "email");
		metadatas.put("annual_salary", "48000");

		Customer customer = new Customer();
		customer.setId(10L);
		customer.setFirstName("Jean");
		customer.setLastName("Dupont");
		customer.setGender(Gender.MALE);
		customer.setMaritalStatus(MaritalStatus.SINGLE);
		customer.setBirthdate(LocalDateTime.of(1995,Month.MAY,3,6,30,40,50000));
		customer.setNationality("FR");
		customer.setAddress("100 avenue de la république");
		customer.setCity("Paris");
		customer.setEmail("jean.dupont@yahoo.fr");
		customer.setPhoneNumber("+33642645678");
		customer.setAccounts(accounts);
		customer.setMetadatas(metadatas);
		
		customer.validate();

		assertThat(customer.getId()).isEqualTo(10);
		assertThat(customer.getFirstName()).hasToString("Jean");
		assertThat(customer.getLastName()).hasToString("Dupont");
		assertThat(customer.getGender()).isEqualTo(Gender.MALE);
		assertThat(customer.getBirthdate()).isBefore(LocalDateTime.now());
		assertThat(customer.getBirthdate().toString()).hasToString("1995-05-03T06:30:40.000050");
		assertThat(customer.getNationality()).isEqualTo("FR");
		assertThat(customer.getMaritalStatus()).isEqualTo(MaritalStatus.SINGLE);
		assertThat(customer.getAddress()).hasToString("100 avenue de la république");
		assertThat(customer.getCity()).hasToString("Paris");
		assertThat(customer.getEmail()).hasToString("jean.dupont@yahoo.fr");
		assertThat(customer.getPhoneNumber()).hasToString("+33642645678");
		assertThat(customer.getAccounts().size()).isEqualTo(1);
		assertThat(customer.getMetadatas().size()).isEqualTo(2);
		assertThat(customer.getMetadatas().get("contact_preferences")).hasToString("email");
		assertThat(customer.getMetadatas().get("annual_salary")).hasToString("48000");
	}

	@Test
	public void validate_should_validateCustomerObject_when_fillRequiredFieldsWithValidValues() {
		Customer customer = new Customer();
		customer.setId(10L);
		customer.setFirstName("Jean");
		customer.setLastName("Dupont");
		customer.setGender(Gender.MALE);
		customer.setMaritalStatus(MaritalStatus.SINGLE);
		customer.setBirthdate(LocalDateTime.of(1995,Month.MAY,3,6,30,40,50000));
		customer.setNationality("FR");
		customer.setAddress("100 avenue de la république");
		customer.setCity("Paris");
		customer.setEmail("jean.dupont@yahoo.fr");
		customer.setPhoneNumber("+33642645678");
		customer.setAccounts(null);
		customer.setMetadatas(null);
		
		customer.validate();
		
		assertThat(customer.getId()).isEqualTo(10);
		assertThat(customer.getFirstName()).hasToString("Jean");
		assertThat(customer.getLastName()).hasToString("Dupont");
		assertThat(customer.getGender()).isEqualTo(Gender.MALE);
		assertThat(customer.getBirthdate()).isBefore(LocalDateTime.now());
		assertThat(customer.getBirthdate().toString()).hasToString("1995-05-03T06:30:40.000050");
		assertThat(customer.getNationality()).isEqualTo("FR");
		assertThat(customer.getMaritalStatus()).isEqualTo(MaritalStatus.SINGLE);
		assertThat(customer.getAddress()).hasToString("100 avenue de la république");
		assertThat(customer.getCity()).hasToString("Paris");
		assertThat(customer.getEmail()).hasToString("jean.dupont@yahoo.fr");
		assertThat(customer.getPhoneNumber()).hasToString("+33642645678");
		assertThat(customer.getAccounts()).isNull();
		assertThat(customer.getMetadatas()).isNull();
	}
	
    @Test
    public void validate_should_throwIllegalStateExceptionWithRequiredFieldsNotBeNullMessage_requiredFieldsAreNull() {
        try {
            Customer customer = new Customer(null, null, null, null, null, null, null, null, null, null, null, null, null);
            customer.validate();

            fail();
        } catch (IllegalStateException exception) {
            String[] errorMessages = exception.getMessage().split("\n");
            assertThat(errorMessages).hasSize(11);
            assertThat(errorMessages[0]).hasToString("id must not be null.");
            assertThat(errorMessages[1]).hasToString("firstName must not be null.");
            assertThat(errorMessages[2]).hasToString("lastName must not be null.");
            assertThat(errorMessages[3]).hasToString("gender must not be null.");
            assertThat(errorMessages[4]).hasToString("maritalStatus must not be null.");
            assertThat(errorMessages[5]).hasToString("birthdate must not be null.");
            assertThat(errorMessages[6]).hasToString("nationality must not be null.");
            assertThat(errorMessages[7]).hasToString("address must not be null.");
            assertThat(errorMessages[8]).hasToString("city must not be null.");
            assertThat(errorMessages[9]).hasToString("email must not be null.");
            assertThat(errorMessages[10]).hasToString("phoneNumber must not be null.");
        }
    }
    
    @Test
    public void validate_should_throwIllegalStateExceptionWithSpecificMessages_when_idIsLowerThanOne() {
        try {
        	Customer customer = new Customer(0L,
                    "Jean",
                    "Dupont",
                    Gender.MALE,
                    MaritalStatus.SINGLE,
                    LocalDateTime.of(1995,Month.MAY,3,6,30,40,50000),
                    "FR",
                    "100 avenue de la république",
                    "Paris",
                    "jean.dupont@yahoo.fr",
                    "+33642645678",
                    null,
                    null);
        	
        	customer.validate();
            fail();
        } catch (IllegalStateException exception) {
            assertThat(exception.getMessage()).hasToString("id must be positive and higher than 0.\n");
        }
    }
    
    @Test
    public void validate_should_throwIllegalStateExceptionWithSpecificMessages_when_firstNameIsEmptyString() {
        try {
        	Customer customer = new Customer(10L,
                    "",
                    "Dupont",
                    Gender.MALE,
                    MaritalStatus.SINGLE,
                    LocalDateTime.of(1995,Month.MAY,3,6,30,40,50000),
                    "FR",
                    "100 avenue de la république",
                    "Paris",
                    "jean.dupont@yahoo.fr",
                    "+33642645678",
                    null,
                    null);

            customer.validate();
            fail();
        } catch (IllegalStateException exception) {
            assertThat(exception.getMessage()).hasToString("firstName must contain at least 1 character.\n");
        }
    }

    @Test
    public void validate_should_throwIllegalStateExceptionWithSpecificMessages_when_firstNameIsTooLong() {
        try {
            String longStr = "Blaine Charles David Earl Frederick Gerald Hubert Irvin John Kenneth Lloyd Martin "
                    + "Nero Oliver Paul Quincy Randolph Sherman Thomas Uncas Victor William Xerxes Yancy Zeus "
                    + "Wolfeschlegelsteinhausenbergerdorffwelchevoralternwarengewissenhaftschaferswessenschaf"
                    + "ewarenwohlgepflegeundsorgfaltigkeitbeschutzenvonangreifendurchihrraubgierigfeindewelch"
                    + "evoralternzwolftausendjahresvorandieerscheinenvanderersteerdemenschderraumschiffgebrau"
                    + "chlichtalsseinursprungvonkraftgestartseinlangefahrthinzwischensternartigraumaufdersuch"
                    + "enachdiesternwelchegehabtbewohnbarplanetenkreisedrehensichundwohinderneurassevonversta"
                    + "ndigmenschlichkeitkonntefortpflanzenundsicherfreuenanlebenslanglichfreudeundruhemitnic"
                    + "hteinfurchtvorangreifenvonandererintelligentgeschopfsvonhinzwischensternartigraum.";

            Customer customer = new Customer(10L,
                    longStr,
                    "Dupont",
                    Gender.MALE,
                    MaritalStatus.SINGLE,
                    LocalDateTime.of(1995,Month.MAY,3,6,30,40,50000),
                    "FR",
                    "100 avenue de la république",
                    "Paris",
                    "jean.dupont@yahoo.fr",
                    "+33642645678",
                    null,
                    null);
            
        	customer.validate();
            fail();
        } catch (IllegalStateException exception) {
            assertThat(exception.getMessage()).hasToString("firstName cannot have more than 750 characters.\n");
        }
    }

    @Test
    public void validate_should_throwIllegalStateExceptionWithSpecificMessages_when_lastNameIsEmptyString() {
        try {
        	Customer customer = new Customer(10L,
                    "Jean",
                    "",
                    Gender.MALE,
                    MaritalStatus.SINGLE,
                    LocalDateTime.of(1995,Month.MAY,3,6,30,40,50000),
                    "FR",
                    "100 avenue de la république", 
                    "Paris", 
                   "jean.dupont@yahoo.fr",
                   "+33642645678",
                   null,
                   null);

        	customer.validate();
            fail();
        } catch (IllegalStateException exception) {
            assertThat(exception.getMessage()).hasToString("lastName  must contain at least 1 character.\n");
        }
    }

    @Test
    public void validate_should_throwIllegalStateExceptionWithSpecificMessages_when_lastNameIsTooLong() {
        try {
            String longStr = "Blaine Charles David Earl Frederick Gerald Hubert Irvin John Kenneth Lloyd Martin "
                    + "Nero Oliver Paul Quincy Randolph Sherman Thomas Uncas Victor William Xerxes Yancy Zeus "
                    + "Wolfeschlegelsteinhausenbergerdorffwelchevoralternwarengewissenhaftschaferswessenschaf"
                    + "ewarenwohlgepflegeundsorgfaltigkeitbeschutzenvonangreifendurchihrraubgierigfeindewelch"
                    + "evoralternzwolftausendjahresvorandieerscheinenvanderersteerdemenschderraumschiffgebrau"
                    + "chlichtalsseinursprungvonkraftgestartseinlangefahrthinzwischensternartigraumaufdersuch"
                    + "enachdiesternwelchegehabtbewohnbarplanetenkreisedrehensichundwohinderneurassevonversta"
                    + "ndigmenschlichkeitkonntefortpflanzenundsicherfreuenanlebenslanglichfreudeundruhemitnic"
                    + "hteinfurchtvorangreifenvonandererintelligentgeschopfsvonhinzwischensternartigraum.";

            Customer customer = new Customer(10L,
                    "Jean",
                    longStr,
                    Gender.MALE,
                    MaritalStatus.SINGLE,
                    LocalDateTime.of(1995,Month.MAY,3,6,30,40,50000),
                    "FR",
                    "100 avenue de la république",
                    "Paris",
                   "jean.dupont@yahoo.fr",
                   "+33642645678",
                   null,
                   null);

        	customer.validate();
            fail();
        } catch (IllegalStateException exception) {
            assertThat(exception.getMessage()).hasToString("lastName cannot have more than 750 characters.\n");
        }
    }
    
    @Test
    public void validate_should_throwIllegalStateExceptionWithSpecificMessages_when_genderOrMaritalStatusAreNull() {
        try {
            Customer customer = new Customer(10L,
                    "Jean",
                    "Dupont",
                    null,
                    null,
                    LocalDateTime.of(1995,Month.MAY,3,6,30,40,50000),
                    "FR",
                    "100 avenue de la république",
                    "Paris",
                    "jean.dupont@yahoo.fr",
                    "+33642645678",
                    null,
                    null);

            customer.validate();
            fail();
        } catch (IllegalStateException exception) {
            assertThat(exception.getMessage()).hasToString("gender must not be null.\nmaritalStatus must not be null.\n");
        }
    }
    
    @Test
    public void validate_should_throwIllegalStateExceptionWithSpecificMessages_when_birthdateIsAfterCurrentDate() {
        try {
        	Customer customer = new Customer(10L,
                    "Jean",
                    "Dupont",
                    Gender.MALE,
                    MaritalStatus.SINGLE,
                    LocalDateTime.of(2099,Month.MAY,3,6,30,40,50000),
                    "FR",
                    "100 avenue de la république",
                    "Paris",
                    "jean.dupont@yahoo.fr",
                    "+33642645678",
                    null,
                    null);

        	customer.validate();
            fail();
        } catch (IllegalStateException exception) {
            assertThat(exception.getMessage()).hasToString("birthdate cannot be null and before the current time.\n");
        }
    }
    
    @Test
    public void validate_should_throwIllegalStateExceptionWithSpecificMessages_when_nationalityIsNotInTheISOCoutries() {
        try {
            Customer customer = new Customer(10L,
                    "Jean",
                    "Dupont",
                    Gender.MALE, 
                   MaritalStatus.SINGLE,
                   LocalDateTime.of(1995,Month.MAY,3,6,30,40,50000),
                   "ABC",
                    "100 avenue de la république",
                    "Paris", 
                   "jean.dupont@yahoo.fr",
                    "+33642645678",
                    null,
                    null);

            customer.validate();
            fail();
        } catch (IllegalStateException exception) {
            assertThat(exception.getMessage()).hasToString("nationality must contain an ISO 3166 country code.\n");
        }
    }
    
    @Test
    public void validate_should_throwIllegalStateExceptionWithSpecificMessages_when_addressIsEmptyString() {
        try {
            Customer customer = new Customer(10L, 
                   "Jean",
                   "Dupont",
                   Gender.MALE,
                   MaritalStatus.SINGLE,
                   LocalDateTime.of(1995,Month.MAY,3,6,30,40,50000),
                   "FR",
                   "",
                    "Paris", 
                   "jean.dupont@yahoo.fr", 
                  "+33642645678",
                  null,
                  null);

            customer.validate();
            fail();
        } catch (IllegalStateException exception) {
            assertThat(exception.getMessage()).hasToString("address must contain at least 1 character.\n");
        }
    }

    @Test
    public void validate_should_throwIllegalStateExceptionWithSpecificMessages_when_cityIsEmptyString() {
        try {
            Customer customer = new Customer(10L, 
                   "Jean",
                    "Dupont",
                    Gender.MALE, 
                    MaritalStatus.SINGLE,
                    LocalDateTime.of(1995,Month.MAY,3,6,30,40,50000),
                    "FR",
                    "100 avenue de la république",
                    "",
                    "jean.dupont@yahoo.fr",
                    "+33642645678",
                    null,
                    null);

            customer.validate();
            fail();
        } catch (IllegalStateException exception) {
            assertThat(exception.getMessage()).hasToString("city must contain at least 1 character.\n");
        }
    }

    @Test
    public void validate_should_throwIllegalStateExceptionWithSpecificMessages_when_emailIsEmptyString() {
        try {
            Customer customer = new Customer(10L,
                    "Jean",
                    "Dupont",
                    Gender.MALE, 
                    MaritalStatus.SINGLE,
                    LocalDateTime.of(1995,Month.MAY,3,6,30,40,50000),
                    "FR",
                    "100 avenue de la république",
                    "Paris",
                    "",
                    "+33642645678",
                    null,
                    null);

            customer.validate();
            fail();
        } catch (IllegalStateException exception) {
            assertThat(exception.getMessage()).hasToString("email must contain at least 1 character.\n");
        }
    }

    @Test
    public void validate_should_throwIllegalStateExceptionWithSpecificMessages_when_phoneNumberIsEmptyString() {
        try {
            Customer customer = new Customer(10L,
                    "Jean", 
                    "Dupont", 
                    Gender.MALE,
                    MaritalStatus.SINGLE,
                    LocalDateTime.of(1995,Month.MAY,3,6,30,40,50000), 
                    "FR",
                    "100 avenue de la république",
                    "Paris",
                    "jean.dupont@yahoo.fr", 
                    "",
                    null,
                    null);
            
            customer.validate();
            fail();
        } catch (IllegalStateException exception) {
            assertThat(exception.getMessage())
                    .hasToString("phoneNumber must contain at least 5 digits and maximum 20 digits.\n");
        }
    }
}

