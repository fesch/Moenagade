/*
    Moenagade
    °°°°°°°°°

    Moenagade is a graphical programming tool for beginners. For this
    reason it is rather limited in its functionality. It aims to help
    students to gain knowledge about the programming structures. Due
    to the explicit conversion to real and executable Java code, it 
    should help to make an easy from block programming to coding.

    Copyright (C) 2009  Bob Fisch

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or any
    later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package lu.fisch.moenagade.model;

import java.util.ArrayList;
import lu.fisch.moenagade.bloxs.Element.Type;
import lu.fisch.moenagade.bloxs.List;

/**
 *
 * @author robert.fisch
 */
public class BloxsDefinitions {
    private ArrayList<BloxsDefinition> definitions = new ArrayList<>();

    public BloxsDefinitions() {
        
        BloxsDefinition bd;
        
        bd = new BloxsDefinition("Structures", "If", "if £", "", BloxsColors.$STRUCTURE, Type.INSTRUCTION, "", true, true, true, "if ($0)\n{\n$$\n}","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Structures", "ElseIf", "else if £", "", BloxsColors.$STRUCTURE, Type.INSTRUCTION, "", true, true, true, "else if ($0)\n{\n$$\n}","");
        bd.getAllowDockAfter().add("If");
        bd.getAllowDockAfter().add("ElseIf");
        bd.nullAllowDockInside();
        definitions.add(bd);
        
        bd = new BloxsDefinition("Structures", "Else", "else", "", BloxsColors.$STRUCTURE, Type.INSTRUCTION, "", true, true, true, "else\n{\n$$\n}","");
        bd.getAllowDockAfter().add("If");
        bd.getAllowDockAfter().add("ElseIf");
        bd.nullAllowDockInside();
        definitions.add(bd);
        
        bd = new BloxsDefinition("Structures", "While", "while £", "", BloxsColors.$STRUCTURE, Type.INSTRUCTION, "", true, true, true, "while ($0)\n{\n$$\n}","");
        definitions.add(bd);
        
         bd = new BloxsDefinition("Structures", "For", "count with § from $ to $ by $", "Variable,double,double,double", BloxsColors.$STRUCTURE, Type.INSTRUCTION, "", true, true, true, "for(int $0=$1; $0<=$2; $0+=$3)\n{\n$$\n}","");
        definitions.add(bd);
        
        
        
        bd = new BloxsDefinition("Values", "True", "true", "", BloxsColors.$BOOLEAN, Type.CONDITION, "boolean", false, false, false, "true","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Values", "False", "false", "", BloxsColors.$BOOLEAN, Type.CONDITION, "boolean", false, false, false, "false","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Values", "Null", "null", "", BloxsColors.$OTHER, Type.EXPRESSION, "boolean", false, false, false, "null","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Values", "KeyCodeLeft", "arrow left", "", BloxsColors.$INPUT, Type.EXPRESSION, "integer", false, false, false, "37","entity");
        bd.setNeedsParent("OnKeyPressed,OnKeyReleased");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Values", "KeyCodeRight", "arrow right", "", BloxsColors.$INPUT, Type.EXPRESSION, "integer", false, false, false, "39","entity");
        bd.setNeedsParent("OnKeyPressed,OnKeyReleased");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Values", "KeyCodeUp", "arrow up", "", BloxsColors.$INPUT, Type.EXPRESSION, "integer", false, false, false, "38","entity");
        bd.setNeedsParent("OnKeyPressed,OnKeyReleased");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Values", "KeyCodeDown", "arrow down", "", BloxsColors.$INPUT, Type.EXPRESSION, "integer", false, false, false, "40","entity");
        bd.setNeedsParent("OnKeyPressed,OnKeyReleased");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Values", "KeySpace", "space key", "", BloxsColors.$INPUT, Type.EXPRESSION, "integer", false, false, false, "32","entity");
        bd.setNeedsParent("OnKeyPressed,OnKeyReleased");
        definitions.add(bd);
        

        
        bd = new BloxsDefinition("Logical", "And", "£ and £", "", BloxsColors.$BOOLEAN, Type.CONDITION, "boolean", false, false, false, "($0 && $1)","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Logical", "Or", "£ or £", "",BloxsColors.$BOOLEAN, Type.CONDITION, "boolean", false, false, false, "($0 || $1)","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Logical", "Compare", "$ € $", "", BloxsColors.$BOOLEAN, Type.CONDITION, "boolean", false, false, false, "($0 $1 $2)","");
        bd.setList(1, new List("==", new String[] {"==","!=","<","<=",">",">="}));
        bd.setTransformation("compare");
        definitions.add(bd);
        
        
        bd = new BloxsDefinition("Events", "OnCreate", "when created", "", BloxsColors.$EVENT, Type.INSTRUCTION, "", false, true, false, "@Override\nprotected void onCreate()\n{\n$$\n}\n\n","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Events", "OnKeyPressed", "when a key has been pressed", "", BloxsColors.$EVENT, Type.INSTRUCTION, "", false, true, false, "@Override\npublic void keyPressed(KeyEvent keyEvent)\n{\n$$\n}\n\n","entity");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Events", "OnKeyReleased", "when a key has been release", "", BloxsColors.$EVENT, Type.INSTRUCTION, "", false, true, false, "@Override\npublic void keyReleased(KeyEvent keyEvent)\n{\n$$\n}\n\n","entity");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Events", "OnMousePressed", "when a mouse button has been pressed", "", BloxsColors.$EVENT, Type.INSTRUCTION, "", false, true, false, "@Override\nprotected void formMousePressed(java.awt.event.MouseEvent evt)\n{\n    super.formMousePressed(evt);\n$$\n}\n\n","world");
        definitions.add(bd);
 
        bd = new BloxsDefinition("Events", "OnMousePressed", "when a mouse button has been pressed", "", BloxsColors.$EVENT, Type.INSTRUCTION, "", false, true, false, "@Override\nprotected void mousePressed(java.awt.event.MouseEvent evt)\n{\n    super.mousePressed(evt);\n$$\n}\n\n","entity");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Events", "OnTouched", "when touched by someone else", "", BloxsColors.$EVENT, Type.INSTRUCTION, "", false, true, false, "@Override\npublic void onTouched(Entity other)\n{\n$$\n}\n\n","entity");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Events", "OnTouchedEntity", "when touched by €", "", BloxsColors.$EVENT, Type.INSTRUCTION, "", false, true, false, "@Override\npublic void onTouched(Entity other)\n{\n    if(other.getClass().getSimpleName().equals(\"$0\"))\n    {\n$$$\n    }\n}\n\n","entity");
        bd.setTransformation(0,"stringify");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Events", "OnOutOfWorld", "when entity reaches world boundary", "", BloxsColors.$EVENT, Type.INSTRUCTION, "", false, true, false, "@Override\npublic void onOutOfWorld()\n{\n$$\n}\n\n","entity");
        definitions.add(bd);
        
        
        bd = new BloxsDefinition("Input", "UserInteger", "integer §", "", BloxsColors.$INPUT, Type.EXPRESSION, "integer", false, false, false, "$0","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Input", "UserFloat", "float §", "", BloxsColors.$INPUT, Type.EXPRESSION, "float", false, false, false, "$0f","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Input", "UserString", "String §", "", BloxsColors.$INPUT, Type.EXPRESSION, "String", false, false, false, "\"$0\"","");
        bd.setTransformation(0,"stringify");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Input", "KeyCode", "key code", "", BloxsColors.$INPUT, Type.EXPRESSION, "integer", false, false, false, "keyEvent.getKeyCode()","entity");
        bd.setNeedsParent("OnKeyPressed,OnKeyReleased");
        definitions.add(bd);
        
        
        bd = new BloxsDefinition("Math", "MathSum", "$ + $", "double,double", BloxsColors.$MATH, Type.EXPRESSION, "", false, false, false, "($0 + $1)","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Math", "MathDifference", "$ - $", "double,double", BloxsColors.$MATH, Type.EXPRESSION, "", false, false, false, "($0 - $1)","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Math", "MathProduct", "$ * $", "double,double", BloxsColors.$MATH, Type.EXPRESSION, "", false, false, false, "($0 * $1)","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Math", "MathDivision", "$ / $", "double,double", BloxsColors.$MATH, Type.EXPRESSION, "", false, false, false, "($0 / $1)","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Math", "MathModulo", "$ % $", "double,double", BloxsColors.$MATH, Type.EXPRESSION, "", false, false, false, "($0 % $1)","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Math", "MathRandom", "random number between $ and $", "int,int", BloxsColors.$MATH, Type.EXPRESSION, "", false, false, false, "(int)(Math.random() * ($1-$0+1)) + $0","");
        definitions.add(bd);
        
        
        bd = new BloxsDefinition("Variables", "VariableDefinition", "define variable § of type € with value $", "", BloxsColors.$VARIABLE, Type.INSTRUCTION, "", true, false, true, "$1 $0 = $2;","");
        bd.setList(1, new List("int", new String[] {"int","double","boolean","String","long","float","Entity","World"}));
        bd.setTransformation("variable");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Variables", "Variable", "value of variable €", "Variable", BloxsColors.$VARIABLE, Type.EXPRESSION, "", false, false, false, "$0","");
        //bd.setList(0, new List("int", new String[] {"int","double","boolean","String","long","float"}));
        definitions.add(bd);        
        
        bd = new BloxsDefinition("Variables", "Variable", "value of variable €", "Variable", BloxsColors.$VARIABLE, Type.CONDITION, "", false, false, false, "$0","");
        //bd.setList(0, new List("int", new String[] {"int","double","boolean","String","long","float"}));
        definitions.add(bd);        
        
        bd = new BloxsDefinition("Variables", "SetVariable", "set variable € to value $", "Variable,", BloxsColors.$VARIABLE, Type.INSTRUCTION, "", true, false, true, "$0 = $1;","");
        definitions.add(bd);

        bd = new BloxsDefinition("Variables", "VariableIncrement", "increment variable € by $", "Variable,double", BloxsColors.$VARIABLE, Type.INSTRUCTION, "", true, false, true, "$0+=$1;","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Variables", "VariableDecrement", "decrement variable € by $", "Variable,double", BloxsColors.$VARIABLE, Type.INSTRUCTION, "", true, false, true, "$0-=$1;","");
        definitions.add(bd);
        
        
        

        bd = new BloxsDefinition("Attributes", "AttributeDefinition", "define attribute § of type € with value $", "", BloxsColors.$OBJECT, Type.INSTRUCTION, "", false, false, false, "public $1 $0 = $2;\n\n","");
        bd.setList(1, new List("int", new String[] {"int","double","boolean","String","long","float"}));
        bd.setTransformation("attribute");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Attributes", "Attribute", "value of attribute €", "Attribute", BloxsColors.$OBJECT, Type.EXPRESSION, "", false, false, false, "$0","");
        //bd.setList(0, new List("int", new String[] {"int","double","boolean","String","long","float"}));
        definitions.add(bd);
        
        bd = new BloxsDefinition("Attributes", "Attribute", "value of attribute €", "Attribute", BloxsColors.$OBJECT, Type.CONDITION, "", false, false, false, "$0","");
        //bd.setList(0, new List("int", new String[] {"int","double","boolean","String","long","float"}));
        definitions.add(bd);
        
        bd = new BloxsDefinition("Attributes", "SetAttribute", "set attribute € to value $", "Attribute,", BloxsColors.$OBJECT, Type.INSTRUCTION, "", true, false, true, "$0 = $1;","");
        definitions.add(bd);
      
        bd = new BloxsDefinition("Attributes", "AttributeIncrement", "increment attribute € by $", "Attribute,double", BloxsColors.$OBJECT, Type.INSTRUCTION, "", true, false, true, "$0+=$1;","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Attributes", "AttributeDecrement", "decrement attribute € by $", "Attribute,double", BloxsColors.$OBJECT, Type.INSTRUCTION, "", true, false, true, "$0-=$1;","");
        definitions.add(bd);
        

        
        bd = new BloxsDefinition("Sound", "PlaySound", "play sound €", "Sound", BloxsColors.$SOUND, Type.INSTRUCTION, "", true, false, true, "getWorld().playSound(\"$0\");","");
        bd.setTransformation(0,"stringify");
        definitions.add(bd);
        
       
        //bd = new BloxsDefinition("**** TEST ****", "Test", "Test $", "", ColorUtils.getColor("#748C51"), Type.INSTRUCTION, "String", true, false, true, "test($0);","");
        //definitions.add(bd);
        
        bd = new BloxsDefinition("Entity", "MoveLeft", "move left by $ pixels", "int", BloxsColors.$ENTITY, Type.INSTRUCTION, "", true, false, true, "moveLeft($0);","entity");
        definitions.add(bd);

        bd = new BloxsDefinition("Entity", "MoveRight", "move right by $ pixels", "int", BloxsColors.$ENTITY, Type.INSTRUCTION, "", true, false, true, "moveRight($0);","entity");
        definitions.add(bd);

        bd = new BloxsDefinition("Entity", "MoveUp", "move up by $ pixels", "int", BloxsColors.$ENTITY, Type.INSTRUCTION, "", true, false, true, "moveUp($0);","entity");
        definitions.add(bd);

        bd = new BloxsDefinition("Entity", "MoveDown", "move down by $ pixels", "int", BloxsColors.$ENTITY, Type.INSTRUCTION, "", true, false, true, "moveDown($0);","entity");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Entity", "SetX", "set X position $", "int", BloxsColors.$ENTITY, Type.INSTRUCTION, "", true, false, true, "setX($0);","entity");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Entity", "SetY", "set Y position $", "int", BloxsColors.$ENTITY, Type.INSTRUCTION, "", true, false, true, "setY($0);","entity");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Entity", "GetX", "X position", "", BloxsColors.$ENTITY, Type.EXPRESSION, "int", false, false, false, "getX()","entity");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Entity", "GetY", "Y position", "", BloxsColors.$ENTITY, Type.EXPRESSION, "int", false, false, false, "getY()","entity");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Entity", "GetWidth", "width of entity", "", BloxsColors.$ENTITY, Type.EXPRESSION, "int", false, false, false, "getWidth()","entity");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Entity", "GetHeight", "height of entity", "", BloxsColors.$ENTITY, Type.EXPRESSION, "int", false, false, false, "getHeight()","entity");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Entity", "AddEntity", "add entity €", "Entity", BloxsColors.$GAME, Type.INSTRUCTION, "", true, false, true, "addEntity(new $0($this));","world");
        definitions.add(bd);
       
        bd = new BloxsDefinition("Entity", "AddEntity", "add entity € at positoin x=$ y=$", "Entity,int,int", BloxsColors.$GAME, Type.INSTRUCTION, "", true, false, true, "addEntity(new $0($this)).setX($1).setY($2);","world");
        definitions.add(bd);
       
        bd = new BloxsDefinition("Entity", "AddEntity", "add entity €", "Entity", BloxsColors.$GAME, Type.EXPRESSION, "", false, false, false, "addEntity(new $0($this))","world");
        definitions.add(bd);
       
        bd = new BloxsDefinition("Entity", "AddEntity", "add entity €", "Entity", BloxsColors.$GAME, Type.INSTRUCTION, "", true, false, true, "getWorld().addEntity(new $0(getWorld()));","entity");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Entity", "AddEntity", "add entity € at positoin x=$ y=$", "Entity,int,int", BloxsColors.$GAME, Type.INSTRUCTION, "", true, false, true, "getWorld().addEntity(new $0(getWorld())).setX($1).setY($2);","entity");
        definitions.add(bd);
       
       
        bd = new BloxsDefinition("Entity", "DeleteEntity", "delete entity", "", BloxsColors.$GAME, Type.INSTRUCTION, "", true, false, true, "delete();","entity");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Entity", "TouchEntity", "touching €", "Entity", BloxsColors.$GAME, Type.CONDITION, "", false, false, false, "getWorld().isTouching($this,\"$0\")!=null","entity");
        bd.setTransformation(0,"stringify");
        definitions.add(bd);
       
        bd = new BloxsDefinition("Entity", "OnTouchedEntity", "touched by €", "Entity", BloxsColors.$EVENT, Type.CONDITION, "", false, false, false, "other.getClass().getSimpleName().equals(\"$0\")","entity");
        bd.setNeedsParent("OnTouchedEntity");
        definitions.add(bd);
        
        
        

        bd = new BloxsDefinition("World", "SetWorld", "set initial world €", "World", BloxsColors.$MAIN, Type.INSTRUCTION, "", true, false, true, "setWorld(new $0());","main");
        definitions.add(bd);
        
        bd = new BloxsDefinition("World", "SetWorld", "set world €", "World", BloxsColors.$MAIN, Type.INSTRUCTION, "", true, false, true, "getMain().setWorld(new $0());","world");
        definitions.add(bd);
        
        bd = new BloxsDefinition("World", "GetWidth", "width of world", "", BloxsColors.$WORLD, Type.EXPRESSION, "int", false, false, false, "getWorld().getWidth()","entity");
        definitions.add(bd);
        
        bd = new BloxsDefinition("World", "GetHeight", "height of world", "", BloxsColors.$WORLD, Type.EXPRESSION, "int", false, false, false, "getWorld().getHeight()","entity");
        definitions.add(bd);
        
        bd = new BloxsDefinition("World", "GetWidth", "width of world", "", BloxsColors.$WORLD, Type.EXPRESSION, "int", false, false, false, "getWidth()","world");
        definitions.add(bd);
        
        bd = new BloxsDefinition("World", "GetHeight", "height of world", "", BloxsColors.$WORLD, Type.EXPRESSION, "int", false, false, false, "getHeight()","world");
        definitions.add(bd);
        
        bd = new BloxsDefinition("World", "SetWidth", "set width of world $", "", BloxsColors.$WORLD, Type.INSTRUCTION, "int", true, false, true, "setWidth($0);","world");
        definitions.add(bd);
        
        bd = new BloxsDefinition("World", "SetHeight", "set height of world $", "", BloxsColors.$WORLD, Type.INSTRUCTION, "int", true, false, true, "setHeight($0);","world");
        definitions.add(bd);
        
        bd = new BloxsDefinition("World", "MoveLeft", "move left by $ pixels", "int", BloxsColors.$WORLD, Type.INSTRUCTION, "", true, false, true, "moveLeft($0);","world");
        definitions.add(bd);

        bd = new BloxsDefinition("World", "MoveRight", "move right by $ pixels", "int", BloxsColors.$WORLD, Type.INSTRUCTION, "", true, false, true, "moveRight($0);","world");
        definitions.add(bd);

        bd = new BloxsDefinition("World", "MoveUp", "move up by $ pixels", "int", BloxsColors.$WORLD, Type.INSTRUCTION, "", true, false, true, "moveUp($0);","world");
        definitions.add(bd);

        bd = new BloxsDefinition("World", "MoveDown", "move down by $ pixels", "int", BloxsColors.$WORLD, Type.INSTRUCTION, "", true, false, true, "moveDown($0);","world");
        definitions.add(bd);
        
        bd = new BloxsDefinition("World", "SetX", "set X position $", "int", BloxsColors.$WORLD, Type.INSTRUCTION, "", true, false, true, "setWorldX($0);","world");
        definitions.add(bd);
        
        bd = new BloxsDefinition("World", "SetY", "set Y position $", "int", BloxsColors.$WORLD, Type.INSTRUCTION, "", true, false, true, "setWorldY($0);","world");
        definitions.add(bd);
        
        bd = new BloxsDefinition("World", "GetX", "X position", "", BloxsColors.$WORLD, Type.EXPRESSION, "int", false, false, false, "geWorldtX()","world");
        definitions.add(bd);
        
        bd = new BloxsDefinition("World", "GetY", "Y position", "", BloxsColors.$WORLD, Type.EXPRESSION, "int", false, false, false, "getWorldY()","world");
        definitions.add(bd);
        
        bd = new BloxsDefinition("World", "CountEntities", "number of entities in world", "", BloxsColors.$WORLD, Type.EXPRESSION, "int", false, false, false, "countEntities()","world");
        definitions.add(bd);
        
        bd = new BloxsDefinition("World", "CountEntitiesClass", "number of entities in world of class €", "Entity", BloxsColors.$WORLD, Type.EXPRESSION, "int", false, false, false, "countEntities(\"$0\")","world");
        bd.setTransformation(0,"stringify");
        definitions.add(bd);
        
        
        
        bd = new BloxsDefinition("Graphical", "LoadImage", "load image €", "Image", BloxsColors.$GRAPHICAL, Type.INSTRUCTION, "", true, false, true, "loadImage(\"$0\");","world,entity");
        //bd.setList(0, new List("int", new String[] {"int","double","boolean","String","long","float"}));
        definitions.add(bd);

        
        
        bd = new BloxsDefinition("Application", "SetWindowSize", "set window size to $ x $ pixels", "int,int", BloxsColors.$MAIN, Type.INSTRUCTION, "", true, false, true, "setWindowSize($0, $1);","main");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Application", "SetTitle", "set application title §", "", BloxsColors.$MAIN, Type.INSTRUCTION, "", true, false, true, "setTitle(\"$0\");","main");
        bd.setTransformation(0,"stringify");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Application", "GetWidth", "width of application", "", BloxsColors.$WORLD, Type.EXPRESSION, "int", false, false, false, "getWidth()","main");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Application", "GetHeight", "height of application", "", BloxsColors.$WORLD, Type.EXPRESSION, "int", false, false, false, "getHeight()","main");
        definitions.add(bd);
        
        
        
        bd = new BloxsDefinition("Timer", "StartTimer", "repeat each $ milli seconds", "int", BloxsColors.$OTHER, Type.INSTRUCTION, "", true, true, true, "new Timer($0, new ActionListener() {\n    @Override public void actionPerformed(ActionEvent ae)\n    {\n    $$$\n        if(getWorld()!=null) getWorld().repaint();\n        if(!$this.getWorld().isDisplayable())\n        {\n            ((Timer)ae.getSource()).stop();\n        }\n    }\n}).start();				\n","entity,world");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Timer", "StopTimer", "stop timer", "", BloxsColors.$OTHER, Type.INSTRUCTION, "", true, false, true, "((Timer)ae.getSource()).stop();","entity,world");
        bd.setNeedsParent("StartTimer");
        definitions.add(bd);
        
       
        bd = new BloxsDefinition("Mouse", "GetMouseX", "get mouse x", "int", BloxsColors.$MOUSE, Type.EXPRESSION, "", false, false, false, "getMouseX()","entity,world");
        definitions.add(bd);
       
        bd = new BloxsDefinition("Mouse", "GetMouseY", "get mouse y", "int", BloxsColors.$MOUSE, Type.EXPRESSION, "", false, false, false, "getMouseY()","entity,world");
        definitions.add(bd);
       
        bd = new BloxsDefinition("Mouse", "IsMouseInside", "mouse inside?", "", BloxsColors.$MOUSE, Type.CONDITION, "", false, false, false, "mouseInside()","entity");
        definitions.add(bd);
        
       
        bd = new BloxsDefinition("Debug", "SystemOutPrintln", "print to console $", "", BloxsColors.$DEBUG, Type.INSTRUCTION, "", true, false, true, "System.out.println($0);","");
        definitions.add(bd);
       
        bd = new BloxsDefinition("Debug", "Comment", "comment §", "", BloxsColors.$DEBUG, Type.INSTRUCTION, "", true, false, true, "// $0","");
        definitions.add(bd);
       
        
        
       
    }

    public ArrayList<BloxsDefinition> getDefinitions(String categorie) {
        ArrayList<BloxsDefinition> result = new ArrayList<>();
        
        for (int i = 0; i < definitions.size(); i++) {
            BloxsDefinition bd = definitions.get(i);
            if(bd.getCategorie().equals(categorie)) result.add(bd);
        }
        
        return result;
    }
    
    public ArrayList<String> getCategories()
    {
        ArrayList<String> result = new ArrayList<>();
        
        for (int i = 0; i < definitions.size(); i++) {
            String cat = definitions.get(i).getCategorie();
            if(!result.contains(cat)) result.add(cat);
        }
        
        return result;
    }
    
}