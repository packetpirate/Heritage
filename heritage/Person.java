package heritage;

public class Person {
    public Person() {
        name = "John Doe";
        nationalities = new java.util.HashSet<>();
        nationalities.add("English");
    }
    
    public Person(String name) {
        this.name = name;
        nationalities = new java.util.HashSet<>();
    }
    
    public Person(String name, java.util.Set nationalities) {
        this.name = name;
        this.nationalities = nationalities;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public java.util.Set<String> getNationalities() {
        return nationalities;
    }
    
    public void addNationality(String nationality) {
        nationalities.add(nationality);
    }
    
    public void removeNationality(String nationality) {
        if(nationalities.contains(nationality)) nationalities.remove(nationality);
    }
    
    private String name;
    private java.util.Set<String> nationalities;
}
