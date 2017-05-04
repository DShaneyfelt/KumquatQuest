package interactivefiction;

public class Thing {

    String name;
    
    public Thing(String name) {
        this.name = name;
    }
    
    public String examine() {
        return "A perfectly average " + name + ".";
    }

    public String put() {
        return "Could not put " + name + " anywhere.";
    }
    
    public String use() {
        
        if (name.equals("kumquat") && Globals.currentSpace == Globals.Space.BEACH) {
            Globals.kumquatIsHelicopter = true;
            InteractiveFiction.getInventory().remove("kumquat");
            return "The kumquat magically transformed into a helicopter! You\'re free to board the helicopter and leave the island.";
        }
        
        return "Could not use " + name + " here.";
    }
    
    public String take() {
        return "Took " + name + ".";
    }
    
    public String getName() {
        return name;
    }
    
}
