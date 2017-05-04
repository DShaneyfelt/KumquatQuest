// Here is a lot of starter code. Try to figure out how it all works together, 
// and how you might change or add on to it to make your own game.
// Be aware that there are probably a number of errors to be accounted for.
// THE CURSE OF KUMQUAT ISLAND pre-alpha alpha v0.000001

package interactivefiction;

import interactivefiction.Globals.Space;
import static interactivefiction.Globals.Space.BEACH;
import static interactivefiction.Globals.Space.BEDROOM1;
import static interactivefiction.Globals.currentSpace;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultCaret;

public final class InteractiveFiction {

    int width = 640;
    int height = 480;
    
    JFrame frame;
    JPanel panel;
    JScrollPane scrollPane;
    
    JTextField comField;
    JTextArea storyField;
    JButton goButton;
    
    static HashMap<String, Thing> inventory;
    static HashMap<String, Thing> spaceElements;
    
    enum GameDirection {NORTH, SOUTH, EAST, WEST};
    
    public InteractiveFiction() {
        initComponents();
        createAndShowGUI();
    }
    
    public void createAndShowGUI() {
        
        frame = new JFrame("KumquatQuest");
        panel = new JPanel();
        
        comField = new JTextField();
        
        storyField = new JTextArea("Consciousness slowly flows into you, and as you become aware of your surroundings you feel the \n" +
"terrible pang of hunger in your abdomen, aswell as the better-left-undescribed need for the bathroom.\n" +
"\n" +
"The room is dark, but the kumquat-shaped digital clock on your nightstand reads \"1:30 AM.\"\n" +
"\n" +
"You get up from your bed, carefully feeling around so as to not bump into any of your furniture. \n" +
"You need to use the bathroom, and you need to find something to eat before\n" +
"you return to bed. But first of all, you need to find a way out of your dark room.\n" + 
"\n" + 
"Type Help, Commands, or H at any time to see what you can do.\n");
        storyField.setEditable(false);
        storyField.setWrapStyleWord(true);
        storyField.setLineWrap(true);
        storyField.setBorder(BorderFactory.createCompoundBorder(
                storyField.getBorder(), 
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        
        scrollPane = new JScrollPane(storyField);
        DefaultCaret caret = (DefaultCaret) storyField.getCaret();
        caret.setUpdatePolicy(DefaultCaret.OUT_BOTTOM);
        
        goButton = new JButton("Go!");
        goButton.addActionListener((ActionEvent e) -> {
            String[] commands = parse(comField.getText());
            if (commands != null) {
                //storyField.append(comField.getText() + "\n");
                String result = execute(commands);
                if (result != null) {
                    if (!result.isEmpty()) storyField.append("\n" + result + "\n");
                }
            }
            storyField.setCaretPosition(storyField.getDocument().getLength());
            comField.setText(null);
        });
        frame.getRootPane().setDefaultButton(goButton);
        
        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        panel.setLayout(gridBagLayout);
        
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(scrollPane, c);
        
        c.gridwidth = GridBagConstraints.HORIZONTAL;
        c.weightx = .9;
        c.weighty = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        panel.add(comField, c);
        
        c.weightx = .1;
        c.gridx = 1;
        c.gridwidth = 1;
        panel.add(goButton, c);
        
        frame.add(panel);
        
        frame.setSize(width, height);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        comField.requestFocus();
    }
    
    public void initComponents() {
        inventory = new HashMap<String, Thing>() {};
        spaceElements = new HashMap<String, Thing>() {};
        
        loadSpace("BEDROOM1");
    }
    
    public void loadSpace(Space space) {
        
        setCurrentSpace(space);
        switch (space) {
            case BEACH:
                getSpaceElements().clear();
                if (!isStored("bucket")) createThing("bucket");
                if (!isStored("seashell")) createThing("seashell");
                if (!isStored("kumquat") && !Globals.kumquatIsHelicopter) createThing("kumquat");
                break;
            case BEDROOM1:
                getSpaceElements().clear();
                if (!isStored("surfboard")) createThing("surfboard");
                if (!isStored("hat")) createThing("hat");
                break;
            default:
                System.err.print("Could not load objects.");
                System.exit(0);
        }
    }
    
    public void loadSpace(String s) {
        loadSpace(stringToSpace(s));
    }
    
    public String printSpaceDesc() {
        
        String temp;
        
        switch(getCurrentSpace()) {
            case BEACH:
                temp = "BEACH\n"
                     + "=====\n"
                     + "The coarse sands are searing hot. To the north is a beach shack. ";
                if (isStored("kumquat"))
                    temp += "Nearby, you see a bucket and a seashell.";
                else
                    temp += "Nearby, you see a bucket, a seashell, and a kumquat.";
                break;
            case BEDROOM1:
                temp = "BEDROOM1\n"
                     + "=====\n"
                     + "Your bedroom is shrouded in darkness, yet soft moonlight shines through a window in the north.\n" +
"Occasionally, you bump into something, hurting your sides and your toes.";
                break;
            default:
                return null;
        }
        
        return temp;
    }
    
    public String printInventory() {
        
        String temp = "INVENTORY:";
        
        if (getInventory().isEmpty()) {
        
            temp += "\n(empty)";
            
        } else for (String key : getInventory().keySet()) {
            temp += "\n" + key;
        }
        
        return temp;
    }
    
    public String printSpaceElements() {
        
        String temp = "THINGS IN THIS SPACE:";
        
        if (getSpaceElements().isEmpty()) {
        
            temp += "\n(nothing)";
            
        } else for (String key : getSpaceElements().keySet()) {
            temp += "\n" + key;
        }
        
        return temp;
    }
    
    public String printCurrentSpace() {
        return "Your current location is the " + getCurrentSpace().toString().toLowerCase() + ".";
    }
    
    public boolean isStored(String s) {
        return getInventory().containsKey(s);
    }
    
    public void createThing(String name) {
        Thing thing = new Thing(name);
        getSpaceElements().put(thing.getName(), thing);
    }
    
    public void createThings(String ... setOfStrings) {
        for (String s : setOfStrings) {
            createThing(s);
        }
    }
    
    public void takeThing(String name) {
        Thing thing = new Thing(name);
        if (getSpaceElements().containsKey(name)) {
            getSpaceElements().get(name).take();
            getSpaceElements().remove(name);
            getInventory().put(thing.getName(), thing);
        }
    }
    
    public void storeThing(String name) {
        Thing thing = new Thing(name);
        if (getSpaceElements().containsKey(name)) {
            getSpaceElements().remove(name);
            getInventory().put(thing.getName(), thing);
        }
    }
    
    public void storeThings(String ... setOfStrings) {
        for (String s : setOfStrings) {
            storeThing(s);
        }
    }
    
    public String[] parse(String s) {
        
        if (s != null) {
            String[] commands = s.split(" ");
            return commands;
        } else {
            return null;
        }
    }
    
    public String execute(String[] s) {
        
        if (s != null) {
            
            // DEPENDANT COMMANDS
            if (s[0] != null) {
                
                // EXAMINE
                // MODIFY TO CHECK BOTH INVENTORY AND SPACEELEMENTS
                if (s[0].equals("examine")) {
                    if (s.length >= 2) {
                        if (getInventory().containsKey(s[1])) {
                            return getInventory().get(s[1]).examine();
                        } else {
                            return "I don't know what that is.";
                        }
                    } else {
                        return "Examine what?";
                    }
                }
                
                // TAKE
                else if (s[0].equals("take")) {
                    if (s.length >= 2) {
                        if (getSpaceElements().containsKey(s[1])) {
                            String temp = getSpaceElements().get(s[1]).take();
                            takeThing(s[1]);
                            return temp;
                        } else {
                            return "I don't see a " + s[1] + " anywhere.";
                        }
                    } else {
                        return "Take what?";
                    }
                }
                
                // USE
                else if (s[0].equals("use")) {
                    if (s.length >= 2) {
                        if (getInventory().containsKey(s[1])) {
                            return getInventory().get(s[1]).use();
                        } else {
                            return "You do not have a " + s[1] + ".";
                        }
                    } else {
                        return "Use what?";
                    }
                }
                
                // BOARD
                else if (s[0].equals("board")) {
                    if (getCurrentSpace() == Space.BEACH && Globals.kumquatIsHelicopter == true) {
                        Globals.victory = true;
                        return "You boarded the helicopter and flew yourself back to civilization. You win!";
                    } else {
                        return "There's nothing to board.";
                    }
                }
                
                // INVENTORY
                else if (s[0].equals("inventory") || s[0].equals("inv") || s[0].equals("i")) {
                    return printInventory();
                }
                
                // SCAN
                else if (s[0].equals("scan")) {
                    return printSpaceElements();
                }
                
                // WHERE
                else if (s[0].equals("where")) {
                    return printSpaceDesc();
                    //return printCurrentSpace();
                }
                
                // NORTH
                else if (s[0].equals("north")) {
                    return moveDirection(GameDirection.NORTH);
                }

                // SOUTH
                else if (s[0].equals("south")) {
                    return moveDirection(GameDirection.SOUTH);
                }
                
                // HELP
                else if (s[0].equals("help") || s[0].equals("h") || s[0].equals("commands")) {
                    return "You can EXAMINE objects objects in your inventory, TAKE objects, USE objects, "
                            + "view your INVENTORY with INV or I, SCAN the area for objects, wonder WHERE you are, ask for HELP, COMMANDS, or H, and go NORTH, SOUTH, EAST, and WEST.";
                }
            }
        }
        
        return null;
    }
    
    public String moveDirection(GameDirection dir) {
        if (Globals.currentSpace == Space.BEACH) {
            switch (dir) {
                case NORTH:
                    loadSpace(Space.BEDROOM1);
                    return printSpaceDesc();
                default:
                    return "You can't go that way.";
            }
        }
        else if (Globals.currentSpace == Space.BEDROOM1) {
            switch (dir) {
                case SOUTH:
                    loadSpace(Space.BEACH);
                    return printSpaceDesc();
                default:
                    return "You can't go that way.";
            }
        }
        else {
            return "Error: cannot determine current location.";
        }
    }

    public Space getCurrentSpace() {
        return currentSpace;
    }

    public void setCurrentSpace(Space s) {
        Globals.currentSpace = s;
    }
    
    public Space stringToSpace(String s) {
        for (Space space : Space.values()) {
            if (space.toString().equalsIgnoreCase(s)) {
                return space;
            }
        }
        return null;
    }

    public static HashMap<String, Thing> getInventory() {
        return inventory;
    }

    public static HashMap<String, Thing> getSpaceElements() {
        return spaceElements;
    }
    
    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(() -> {
            InteractiveFiction interactiveFiction = new InteractiveFiction();
        });
    }
}
