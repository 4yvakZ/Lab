package PostgresSQL;

public final class PasswordGenerator {

    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String PUNCTUATION = "!@#$%&*()_+-=[]|,./?><";
    private boolean useUpper;
    private boolean useDigits;
    private boolean usePunctuation;

    public PasswordGenerator(boolean useUpper, boolean useDigits, boolean usePunctuation) {
        this.useUpper = useUpper;
        this.useDigits = useDigits;
        this.usePunctuation = usePunctuation;
    }

    public String generatePassword(int length){
        StringBuilder password = new StringBuilder();
        char[] symbols = generateSymbolsString().toCharArray();
        while (password.length()<length){
            password.append(symbols[(int)(Math.random()*symbols.length)]);
        }
        return password.toString();
    }

    private String generateSymbolsString(){
        String symbols = LOWER;
        if(useDigits) symbols+=DIGITS;
        if(useUpper) symbols+=UPPER;
        if(usePunctuation) symbols+=PUNCTUATION;
        return symbols;
    }
}