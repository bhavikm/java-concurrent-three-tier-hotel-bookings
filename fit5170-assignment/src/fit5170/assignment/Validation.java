package fit5170.assignment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DecimalFormatSymbols;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Validation class is coupled with the Broker in the middleware layer
 * This allows a generic client to connect with the Broker and have the same
 * validation for user inputs applied
 * 
 * @author Bhavik Maneck
 */
public class Validation {
    public final static int MAX_NAME_SIZE = 30;
    public final static int MAX_EMAIL_SIZE = 100;
    
    public static String validateCreditCard(String creditCard)
    {
        String error = "None";
        
        if (creditCard.length() != 16)
        {
            error = "Invalid Credit Card length, must be 16 numbers";
        } else if (!isStringIntegerPositive(creditCard)) {
            error = "Credit Card must be a 16 digit number";
        }
        
        return error;
    }
    
    public static String validatePhoneNumber(String phone)
    {
        String error = "None";
        
        if ((phone.length() != 10) && (phone.length() != 8))
        {
            error = "Phone length, must be 8 or 10 numbers";
        } else if (!isStringIntegerPositive(phone)) {
            error = "Phone must be a number";
        }
        
        return error;
    }
    
    public static String validateName(String name)
    {
        String error = "None";
        
        if (name.length() > MAX_NAME_SIZE || name.trim().length() == 0)
        {
            error = "Names must be "+MAX_NAME_SIZE+" characters or less.";
        }
        
        return error;
    }
    
    public static String validateEmail(String email)
    {
        String error = "None";
        
        //Regex for testing email string validity, taken from: http://stackoverflow.com/questions/16295329/email-address-validation-regex
        String emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        
        if (email.length() > MAX_EMAIL_SIZE || email.trim().length() == 0)
        {
            error = "Email must be "+MAX_EMAIL_SIZE+" characters or less.";
        } else if (!matcher.matches()) {
            error = "Not a valid email format.";
        }
        
        return error;
    }
    
    public static boolean isStringNumeric(String str)
    {
        DecimalFormatSymbols currentLocaleSymbols = DecimalFormatSymbols.getInstance();
        char localeMinusSign = currentLocaleSymbols.getMinusSign();

        if (!Character.isDigit(str.charAt(0)) && str.charAt(0) != localeMinusSign ) return false;

        boolean isDecimalSeparatorFound = false;
        char localeDecimalSeparator = currentLocaleSymbols.getDecimalSeparator();

        for (char c : str.substring(1).toCharArray())
        {
            if (!Character.isDigit(c))
            {
                if (c == localeDecimalSeparator && !isDecimalSeparatorFound)
                {
                    isDecimalSeparatorFound = true;
                    continue;
                }
                return false;
            }
        }
        return true;
    }
    
    public static boolean isStringNumericPositive(String str)
    {
        DecimalFormatSymbols currentLocaleSymbols = DecimalFormatSymbols.getInstance();

        if (!Character.isDigit(str.charAt(0))) return false;

        boolean isDecimalSeparatorFound = false;
        char localeDecimalSeparator = currentLocaleSymbols.getDecimalSeparator();

        for (char c : str.substring(1).toCharArray())
        {
            if (!Character.isDigit(c))
            {
                if (c == localeDecimalSeparator && !isDecimalSeparatorFound)
                {
                    isDecimalSeparatorFound = true;
                    continue;
                }
                return false;
            }
        }
        return true;
    }
    
    public static boolean isStringIntegerPositive(String str)
    {
        for (char c : str.substring(0).toCharArray())
        {
            if (!Character.isDigit(c))
            {
                return false;
            }
        }
        return true;
    }
    
    //Generic function for checking if string is a date without a formatter
    public static boolean isStringADate(String str) {
        String DATE_PATTERN = "(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)";
        Pattern pattern = Pattern.compile(DATE_PATTERN);
        Matcher matcher = pattern.matcher(str);
         
        if(matcher.matches()){
            return true;
        } else {
            return false;
        }
    }
    
    
    public static String validateDateInputs(String dateCheckIn, String dateCheckOut) {
        String dateError = "None";
        
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date todaysDate = new Date();
        Date checkIn = new Date();
        Date checkOut = new Date();
        try {
            checkIn = formatter.parse(dateCheckIn);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            checkOut = formatter.parse(dateCheckOut);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        if (dateCheckIn.trim().length() == 0 || dateCheckOut.trim().length() == 0)
        {
            dateError = "Dates must not be empty.";
        } else if (!DateValidator.isThisDateValid(dateCheckIn, "dd/MM/yyyy") || 
                   !DateValidator.isThisDateValid(dateCheckOut, "dd/MM/yyyy")) {
            dateError = "Date format is invalid. Please use dd/mm/yyyy";
        } else if (todaysDate.after(checkIn) || todaysDate.after(checkOut)) {
            dateError = "Dates must be after today";
        } else if (checkIn.after(checkOut) || dateCheckIn.equals(dateCheckOut)) {
            dateError = "Check out date must be after check in date.";
        }
            
        return dateError;
    }
}
