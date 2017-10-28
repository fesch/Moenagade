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

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import lu.fisch.moenagade.bloxs.Element.Type;
import lu.fisch.moenagade.bloxs.List;

/**
 *
 * @author robert.fisch
 */
public class BloxsDefinitions {
    private ArrayList<BloxsDefinition> definitions = new ArrayList<>();
    private HashMap<String,Color> catColors = new HashMap<>();

    public BloxsDefinitions() {
        
        BloxsDefinition bd;
        BloxsDefinition sbd;
        
        
        
        catColors.put("Structures", BloxsColors.$STRUCTURE);
        
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
        
        
        
        catColors.put("Values", BloxsColors.$BACKGROUND);
        
        bd = new BloxsDefinition("Values", "UserInteger", "integer §", "", BloxsColors.$INPUT, Type.EXPRESSION, "integer", false, false, false, "$0","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Values", "UserFloat", "decimal §", "", BloxsColors.$INPUT, Type.EXPRESSION, "float", false, false, false, "$0f","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Values", "UserString", "String §", "", BloxsColors.$INPUT, Type.EXPRESSION, "String", false, false, false, "\"$0\"","");
        bd.setTransformation(0,"stringify");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Values", "True", "true", "", BloxsColors.$BOOLEAN, Type.CONDITION, "boolean", false, false, false, "true","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Values", "False", "false", "", BloxsColors.$BOOLEAN, Type.CONDITION, "boolean", false, false, false, "false","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Values", "Null", "null", "", BloxsColors.$OTHER, Type.EXPRESSION, "", false, false, false, "null","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Values", "StringValueOf", "value of $ as String", "double", BloxsColors.$INPUT, Type.EXPRESSION, "String", false, false, false, "String.valueOf($0)","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Values", "IntValueOf", "value of $ as integer", "String", BloxsColors.$INPUT, Type.EXPRESSION, "int", false, false, false, "Integer.valueOf($0)","");
        definitions.add(bd);
        
        
        
        
        
        catColors.put("Logical", BloxsColors.$BOOLEAN);

        bd = new BloxsDefinition("Logical", "And", "£ and £", "", BloxsColors.$BOOLEAN, Type.CONDITION, "boolean", false, false, false, "($0 && $1)","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Logical", "Or", "£ or £", "",BloxsColors.$BOOLEAN, Type.CONDITION, "boolean", false, false, false, "($0 || $1)","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Logical", "Not", "not £", "", BloxsColors.$BOOLEAN, Type.CONDITION, "boolean", false, false, false, "!($0)","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Logical", "Compare", "$ € $", "", BloxsColors.$BOOLEAN, Type.CONDITION, "boolean", false, false, false, "($0 $1 $2)","");
        bd.setList(1, new List("==", new String[] {"==","!=","<","<=",">",">="}));
        bd.setTransformation("compare");
        definitions.add(bd);
                
        bd = new BloxsDefinition("Logical", "InstanceOf", "$ instance of €", "Entity,Entity",BloxsColors.$BOOLEAN, Type.CONDITION, "boolean", false, false, false, "($0 instanceof $1)","");
        definitions.add(bd);
        
        
        
        catColors.put("Events", BloxsColors.$EVENT);
        
        bd = new BloxsDefinition("Events", "OnCreate", "when created", "", BloxsColors.$EVENT, Type.INSTRUCTION, "", false, true, false, "@Override\nprotected void onCreate()\n{\n$$\n}\n\n","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Events", "OnDraw", "when being painted", "", BloxsColors.$EVENT, Type.INSTRUCTION, "", false, true, false, "@Override\npublic void onDraw(Graphics g)\n{\n$$\n}\n\n","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Events", "OnKeyPressed", "when key has been pressed `", "", BloxsColors.$EVENT, Type.INSTRUCTION, "", false, true, false, "@Override\npublic void keyPressed(KeyEvent keyEvent)\n{\n$$\n}\n\n","entity");
        definitions.add(bd);
        
            sbd = new BloxsDefinition("Input", "KeyCode", "key code", "", BloxsColors.$INPUT, Type.EXPRESSION, "integer", false, false, false, "keyEvent.getKeyCode()","entity");
            sbd.setNeedsParent("OnKeyPressed,OnKeyReleased");
            bd.addSub(0, sbd);
        
        
        bd = new BloxsDefinition("Events", "OnKeyReleased", "when key has been released `", "", BloxsColors.$EVENT, Type.INSTRUCTION, "", false, true, false, "@Override\npublic void keyReleased(KeyEvent keyEvent)\n{\n$$\n}\n\n","entity");
        definitions.add(bd);
        
            sbd = new BloxsDefinition("Input", "KeyCode", "key code", "", BloxsColors.$INPUT, Type.EXPRESSION, "integer", false, false, false, "keyEvent.getKeyCode()","entity");
            sbd.setNeedsParent("OnKeyPressed,OnKeyReleased");
            bd.addSub(0, sbd);
        
        //bd = new BloxsDefinition("Events", "OnMousePressed", "when a mouse button has been pressed", "", BloxsColors.$EVENT, Type.INSTRUCTION, "", false, true, false, "@Override\nprotected void formMousePressed(java.awt.event.MouseEvent evt)\n{\n    super.formMousePressed(evt);\n$$\n}\n\n","world");
        //definitions.add(bd);
 
        bd = new BloxsDefinition("Events", "OnMousePressed", "when mouse button has been pressed `", "", BloxsColors.$EVENT, Type.INSTRUCTION, "", false, true, false, "@Override\nprotected void onMousePressed(java.awt.event.MouseEvent evt)\n{\n    super.onMousePressed(evt);\n$$\n}\n\n","entity,world");
        definitions.add(bd);
        
            sbd = new BloxsDefinition("Mouse", "GetMouseX", "get mouse x", "int", BloxsColors.$MOUSE, Type.EXPRESSION, "", false, false, false, "getMouseX()","entity,world");
            bd.addSub(0, sbd);

            sbd = new BloxsDefinition("Mouse", "GetMouseY", "get mouse y", "int", BloxsColors.$MOUSE, Type.EXPRESSION, "", false, false, false, "getMouseY()","entity,world");
            bd.addSub(0, sbd);
        
        bd = new BloxsDefinition("Events", "OnMouseReleased", "when mouse button has been released `", "", BloxsColors.$EVENT, Type.INSTRUCTION, "", false, true, false, "@Override\nprotected void onMouseReleased(java.awt.event.MouseEvent evt)\n{\n    super.onMouseReleased(evt);\n$$\n}\n\n","entity,world");
        definitions.add(bd);
        
            sbd = new BloxsDefinition("Mouse", "GetMouseX", "get mouse x", "int", BloxsColors.$MOUSE, Type.EXPRESSION, "", false, false, false, "getMouseX()","entity,world");
            bd.addSub(0, sbd);

            sbd = new BloxsDefinition("Mouse", "GetMouseY", "get mouse y", "int", BloxsColors.$MOUSE, Type.EXPRESSION, "", false, false, false, "getMouseY()","entity,world");
            bd.addSub(0, sbd);
        
        bd = new BloxsDefinition("Events", "OnMouseMoved", "when mouse has been moved `", "", BloxsColors.$EVENT, Type.INSTRUCTION, "", false, true, false, "@Override\nprotected void onMouseMoved(java.awt.event.MouseEvent evt)\n{\n    super.onMouseMoved(evt);\n$$\n}\n\n","entity,world");
        definitions.add(bd);
        
            sbd = new BloxsDefinition("Mouse", "GetMouseX", "get mouse x", "int", BloxsColors.$MOUSE, Type.EXPRESSION, "", false, false, false, "getMouseX()","entity,world");
            bd.addSub(0, sbd);

            sbd = new BloxsDefinition("Mouse", "GetMouseY", "get mouse y", "int", BloxsColors.$MOUSE, Type.EXPRESSION, "", false, false, false, "getMouseY()","entity,world");
            bd.addSub(0, sbd);
        
        bd = new BloxsDefinition("Events", "OnMouseDragged", "when mouse has been dragged `", "", BloxsColors.$EVENT, Type.INSTRUCTION, "", false, true, false, "@Override\nprotected void onMouseDragged(java.awt.event.MouseEvent evt)\n{\n    super.onMouseDragged(evt);\n$$\n}\n\n","entity,world");
        definitions.add(bd);
        
            sbd = new BloxsDefinition("Mouse", "GetMouseX", "get mouse x", "int", BloxsColors.$MOUSE, Type.EXPRESSION, "", false, false, false, "getMouseX()","entity,world");
            bd.addSub(0, sbd);

            sbd = new BloxsDefinition("Mouse", "GetMouseY", "get mouse y", "int", BloxsColors.$MOUSE, Type.EXPRESSION, "", false, false, false, "getMouseY()","entity,world");
            bd.addSub(0, sbd);
        
        bd = new BloxsDefinition("Events", "OnTouchedEntity", "when touched by `", "", BloxsColors.$EVENT, Type.INSTRUCTION, "", false, true, false, "@Override\npublic void onTouched(Entity other)\n{\n$$\n}\n\n","entity");
        definitions.add(bd);
        
            sbd = new BloxsDefinition("Mouse", "getEntity", "entity", "Entity", BloxsColors.$MOUSE, Type.EXPRESSION, "", false, false, false, "other","entity,world");
            sbd.setNeedsParent("OnTouchedEntity");
            bd.addSub(0, sbd);
        
        bd = new BloxsDefinition("Events", "OnTouchedEntity", "when touched by €", "Entity", BloxsColors.$EVENT, Type.INSTRUCTION, "", false, true, false, "@Override\npublic void onTouched(Entity other)\n{\n    if(other.getClass().getSimpleName().equals(\"$0\"))\n    {\n$$$\n    }\n}\n\n","entity");
        bd.setTransformation(0,"stringify");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Events", "OnOutOfWorld", "when entity is outside the world", "", BloxsColors.$EVENT, Type.INSTRUCTION, "", false, true, false, "@Override\npublic void onOutOfWorld()\n{\n$$\n}\n\n","entity");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Events", "OnTouchBoundary", "when entity reaches world boundary", "", BloxsColors.$EVENT, Type.INSTRUCTION, "", false, true, false, "@Override\npublic void onTouchBoundary()\n{\n$$\n}\n\n","entity");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Events", "OnPositionChanged", "when the position has changed", "", BloxsColors.$EVENT, Type.INSTRUCTION, "", false, true, false, "@Override\npublic void onPositionChanged()\n{\n$$\n}\n\n","entity");
        definitions.add(bd);
        
        
        
        catColors.put("Methods", BloxsColors.$METHOD);
        
        bd = new BloxsDefinition("Methods", "MethodDefinition", "method §", "", BloxsColors.$METHOD, Type.INSTRUCTION, "", false, true, false, "public void $0()\n{\n$$\n}\n\n","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Methods", "MethodDefinition", "method § returns €", ",Types", BloxsColors.$METHOD, Type.INSTRUCTION, "", false, true, false, "public $1 $0()\n{\n$$\n}\n\n","");
        //bd.setList(1, new List("int", new String[] {"int","double","boolean","String","long","float"}));
        definitions.add(bd);
        
        bd = new BloxsDefinition("Methods", "MethodDefinition", "method § returns € with parameters ^", ",Types,Parameters", BloxsColors.$METHOD, Type.INSTRUCTION, "", false, true, false, "public $1 $0($2)\n{\n$$\n}\n\n","");
        //bd.setList(1, new List("int", new String[] {"int","double","boolean","String","long","float"}));
        definitions.add(bd);
        
        bd = new BloxsDefinition("Methods", "Return", "return $", "", BloxsColors.$METHOD, Type.INSTRUCTION, "", true, false, false, "return $0;\n","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Methods", "Return", "return £", "", BloxsColors.$METHOD, Type.INSTRUCTION, "", true, false, false, "return $0;\n","");
        bd.setNeedsParent("MethodDefinition");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Methods", "MethodCall", "call method €", "MethodList", BloxsColors.$METHOD, Type.INSTRUCTION, "", true, false, true, "$0();\n","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Methods", "MethodCall", "call method €", "MethodList", BloxsColors.$METHOD, Type.EXPRESSION, "", true, false, true, "$0();\n","");
        definitions.add(bd);
        
        
        
        catColors.put("Keyboard", BloxsColors.$INPUT);
        
        bd = new BloxsDefinition("Keyboard", "KeyCode", "key code", "", BloxsColors.$INPUT, Type.EXPRESSION, "integer", false, false, false, "keyEvent.getKeyCode()","entity");
        bd.setNeedsParent("OnKeyPressed,OnKeyReleased");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Keyboard", "KeyCodeLeft", "arrow left", "", BloxsColors.$INPUT, Type.EXPRESSION, "integer", false, false, false, "37","entity");
        bd.setNeedsParent("OnKeyPressed,OnKeyReleased");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Keyboard", "KeyCodeRight", "arrow right", "", BloxsColors.$INPUT, Type.EXPRESSION, "integer", false, false, false, "39","entity");
        bd.setNeedsParent("OnKeyPressed,OnKeyReleased");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Keyboard", "KeyCodeUp", "arrow up", "", BloxsColors.$INPUT, Type.EXPRESSION, "integer", false, false, false, "38","entity");
        bd.setNeedsParent("OnKeyPressed,OnKeyReleased");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Keyboard", "KeyCodeDown", "arrow down", "", BloxsColors.$INPUT, Type.EXPRESSION, "integer", false, false, false, "40","entity");
        bd.setNeedsParent("OnKeyPressed,OnKeyReleased");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Keyboard", "KeySpace", "space key", "", BloxsColors.$INPUT, Type.EXPRESSION, "integer", false, false, false, "32","entity");
        bd.setNeedsParent("OnKeyPressed,OnKeyReleased");
        definitions.add(bd);
        
        
        
        catColors.put("Math", BloxsColors.$MATH);
        
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
        
        bd = new BloxsDefinition("Math", "MathCos", "cosinus of $ °", "double", BloxsColors.$MATH, Type.EXPRESSION, "double", false, false, false, "Math.cos($0)","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Math", "MathSin", "sinus of $ °", "double", BloxsColors.$MATH, Type.EXPRESSION, "double", false, false, false, "Math.sin($0)","");
        definitions.add(bd);

        bd = new BloxsDefinition("Math", "MathAtan2", "atan2 of $ and $", "double,double", BloxsColors.$MATH, Type.EXPRESSION, "double", false, false, false, "Math.atan2($0,$1)","");
        definitions.add(bd);

        bd = new BloxsDefinition("Math", "MathFloor", "integer value of $", "double", BloxsColors.$MATH, Type.EXPRESSION, "int", false, false, false, "(int) $0","");
        definitions.add(bd);
        
        
        
        catColors.put("Variables", BloxsColors.$VARIABLE);
        
        bd = new BloxsDefinition("Variables", "VariableDefinition", "define variable § of type € with value $", ",Type,", BloxsColors.$VARIABLE, Type.INSTRUCTION, "", true, false, true, "$1 $0 = $2;","");
        //bd.setList(1, new List("int", new String[] {"int","double","boolean","String","long","float"}));
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
        
        
        
        catColors.put("Attributes", BloxsColors.$OBJECT);

        bd = new BloxsDefinition("Attributes", "AttributeDefinition", "define attribute § of type € with value $", ",Type,", BloxsColors.$OBJECT, Type.INSTRUCTION, "", false, false, false, "private $1 $0 = ($1) $2;\n\npublic void set$0($1 $0)\n{\n    this.$0=$0;\n}\n\npublic $1 get$0()\n{\n    return $0;\n}\n\n","");
        //bd.setList(1, new List("int", new String[] {"int","double","boolean","String","long","float"}));
        bd.setTransformation("attribute");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Attributes", "Attribute", "value of attribute €", "Attribute", BloxsColors.$OBJECT, Type.EXPRESSION, "", false, false, false, "get$0()","");
        //bd.setList(0, new List("int", new String[] {"int","double","boolean","String","long","float"}));
        definitions.add(bd);
        
        bd = new BloxsDefinition("Attributes", "Attribute", "value of attribute €", "Attribute", BloxsColors.$OBJECT, Type.CONDITION, "", false, false, false, "get$0()","");
        //bd.setList(0, new List("int", new String[] {"int","double","boolean","String","long","float"}));
        definitions.add(bd);
        
        bd = new BloxsDefinition("Attributes", "SetAttribute", "set attribute € to value $", "Attribute,", BloxsColors.$OBJECT, Type.INSTRUCTION, "", true, false, true, "set$0($1);","");
        definitions.add(bd);
      
        bd = new BloxsDefinition("Attributes", "AttributeIncrement", "increment attribute € by $", "Attribute,double", BloxsColors.$OBJECT, Type.INSTRUCTION, "", true, false, true, "$0+=$1;","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Attributes", "AttributeDecrement", "decrement attribute € by $", "Attribute,double", BloxsColors.$OBJECT, Type.INSTRUCTION, "", true, false, true, "$0-=$1;","");
        definitions.add(bd);
        
        
        
        catColors.put("Sound", BloxsColors.$SOUND);
        
        bd = new BloxsDefinition("Sound", "PlaySound", "play sound €", "Sound", BloxsColors.$SOUND, Type.INSTRUCTION, "", true, false, true, "getWorld().playSound(\"$0\");","");
        bd.setTransformation(0,"stringify");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Sound", "PlaySound", "play sound €", "Sound", BloxsColors.$SOUND, Type.EXPRESSION, "Clip", true, false, true, "getWorld().playSound(\"$0\")","");
        bd.setTransformation(0,"stringify");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Sound", "StopSound", "stop sound $", "Clip", BloxsColors.$SOUND, Type.INSTRUCTION, "Clip", true, false, true, "$0.stop();","");
        definitions.add(bd);
        
        
        
        catColors.put("Object", BloxsColors.$OBJECT);
       
        //bd = new BloxsDefinition("**** TEST ****", "Test", "Test $", "", ColorUtils.getColor("#748C51"), Type.INSTRUCTION, "String", true, false, true, "test($0);","");
        //definitions.add(bd);
        
        bd = new BloxsDefinition("Object", "ObjectSetAttribute", "set € . € to $", "EntityList,EntityAttributes,", BloxsColors.$OBJECT, Type.INSTRUCTION, "", true, false, true, "$0.set$1($2);","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Object", "ObjectGetAttribute", "get value of € . €", "EntityList,EntityAttributes", BloxsColors.$OBJECT, Type.EXPRESSION, "", false, false, false, "$0.get$1()","");
        definitions.add(bd);
        
         bd = new BloxsDefinition("Object", "ObjectGetAttribute", "get value of € . €", "EntityList,EntityAttributes", BloxsColors.$OBJECT, Type.CONDITION, "", false, false, false, "$0.get$1()","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Object", "ObjectSetX", "set X position of € to $", "EntityList,int", BloxsColors.$OBJECT, Type.INSTRUCTION, "", true, false, true, "$0.setX($1);","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Object", "ObjectSetY", "set Y position of € to $", "EntityList,int", BloxsColors.$OBJECT, Type.INSTRUCTION, "", true, false, true, "$0.setY($1);","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Object", "ObjectGetX", "get X position of €", "EntityList", BloxsColors.$OBJECT, Type.EXPRESSION, "int", true, false, true, "$0.getX()","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Object", "ObjectGetY", "get Y position of €", "EntityList", BloxsColors.$OBJECT, Type.EXPRESSION, "int", true, false, true, "$0.getY()","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Object", "ObjectMethodCall", "on object € call method €", "EntityList,ObjectMethods", BloxsColors.$OBJECT, Type.INSTRUCTION, "", true, false, true, "$0.$1();","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Object", "ObjectMethodCall", "on object € call method €", "EntityList,ObjectMethods", BloxsColors.$OBJECT, Type.EXPRESSION, "", true, false, true, "$0.$1()","");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Object", "ObjectMethodCall", "on object € call method €", "EntityList,ObjectMethods", BloxsColors.$OBJECT, Type.CONDITION, "", true, false, true, "$0.$1()","");
        definitions.add(bd);
        
        
        
        catColors.put("Entity", BloxsColors.$ENTITY);
        
        bd = new BloxsDefinition("Entity", "BringToFront", "bring to front", "", BloxsColors.$ENTITY, Type.INSTRUCTION, "", true, false, true, "bringToFront();","entity");
        definitions.add(bd);

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
        
        bd = new BloxsDefinition("Entity", "SetX", "set X position of entity $ to $", ",int", BloxsColors.$ENTITY, Type.INSTRUCTION, "", true, false, true, "$0.setX($1);","entity,world");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Entity", "SetY", "set Y position of entity $ to $", ",int", BloxsColors.$ENTITY, Type.INSTRUCTION, "", true, false, true, "$0.setY($1);","entity,world");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Entity", "GetX", "X position of entity $", "", BloxsColors.$ENTITY, Type.EXPRESSION, "int", false, false, false, "$0.getX()","entity,world");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Entity", "GetY", "Y position of entity $", "", BloxsColors.$ENTITY, Type.EXPRESSION, "int", false, false, false, "$0.getY()","entity,world");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Entity", "GetWidth", "width of entity $", "", BloxsColors.$ENTITY, Type.EXPRESSION, "int", false, false, false, "$0.getWidth()","entity,world");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Entity", "GetHeight", "height of entity $", "", BloxsColors.$ENTITY, Type.EXPRESSION, "int", false, false, false, "$0.getHeight()","entity,world");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Entity", "SetWidth", "set width of entity $", "int", BloxsColors.$ENTITY, Type.INSTRUCTION, "int", true, false, true, "setWidth($0);","entity");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Entity", "SetHeight", "set height of entity $", "int", BloxsColors.$ENTITY, Type.INSTRUCTION, "int", true, false, true, "setHeight($0);","entity");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Entity", "SetDimension", "set dimension of entity width= $ height= $", "int,int", BloxsColors.$ENTITY, Type.INSTRUCTION, "int", true, false, true, "setDimension($0,$1);","entity");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Entity", "AddEntity", "add entity €", "Entity", BloxsColors.$GAME, Type.INSTRUCTION, "", true, false, true, "addEntity(new $0($this));","world");
        definitions.add(bd);
       
        bd = new BloxsDefinition("Entity", "AddEntity", "add entity € at position x=$ y=$", "Entity,int,int", BloxsColors.$GAME, Type.INSTRUCTION, "", true, false, true, "addEntity(new $0($this,$1,$2));","world");
        definitions.add(bd);
       
        bd = new BloxsDefinition("Entity", "AddEntity", "add entity €", "Entity", BloxsColors.$GAME, Type.EXPRESSION, "", false, false, false, "addEntity(new $0($this))","world");
        definitions.add(bd);
       
        bd = new BloxsDefinition("Entity", "AddEntity", "add entity €", "Entity", BloxsColors.$GAME, Type.INSTRUCTION, "", true, false, true, "getWorld().addEntity(new $0(getWorld()));","entity");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Entity", "AddEntity", "add entity € at position x=$ y=$", "Entity,int,int", BloxsColors.$GAME, Type.INSTRUCTION, "Entity", true, false, true, "getWorld().addEntity(new $0(getWorld())).setX($1).setY($2);","entity");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Entity", "AddEntity", "add entity €", "Entity", BloxsColors.$GAME, Type.EXPRESSION, "", false, false, false, "getWorld().addEntity(new $0(getWorld()))","entity");
        definitions.add(bd);
       
        bd = new BloxsDefinition("Entity", "DeleteEntity", "delete myself", "", BloxsColors.$GAME, Type.INSTRUCTION, "", true, false, true, "delete();","entity");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Entity", "DeleteEntity", "delete entity $", "Entity", BloxsColors.$GAME, Type.INSTRUCTION, "", true, false, true, "$0.delete();","entity");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Entity", "DeleteEntities", "delete all entities of class €", "Entity", BloxsColors.$GAME, Type.INSTRUCTION, "", true, false, true, "getWorld().deleteAll($0.class);","entity,world");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Entity", "TouchEntity", "touching €", "Entity", BloxsColors.$GAME, Type.CONDITION, "", false, false, false, "getWorld().isTouching($this,\"$0\")!=null","entity");
        bd.setTransformation(0,"stringify");
        definitions.add(bd);
       
        bd = new BloxsDefinition("Entity", "OnTouchedEntity", "touched by €", "Entity", BloxsColors.$EVENT, Type.CONDITION, "", false, false, false, "other.getClass().getSimpleName().equals(\"$0\")","entity");
        bd.setNeedsParent("OnTouchedEntity");
        definitions.add(bd);
        
        
        
        catColors.put("World", BloxsColors.$WORLD);

        bd = new BloxsDefinition("World", "SetWorld", "set initial world €", "World", BloxsColors.$MAIN, Type.INSTRUCTION, "", true, false, true, "setWorld(new $0());","main");
        definitions.add(bd);
        
        bd = new BloxsDefinition("World", "SetWorld", "set world €", "World", BloxsColors.$MAIN, Type.INSTRUCTION, "", true, false, true, "getWorld().getMain().setWorld(new $0());","world,entity");
        definitions.add(bd);
        
        bd = new BloxsDefinition("World", "GetWorld", "get actual world", "World", BloxsColors.$WORLD, Type.EXPRESSION, "", false, false, false, "getWorld()","entity");
        definitions.add(bd);
        
        bd = new BloxsDefinition("World", "GetWidth", "width of world", "", BloxsColors.$WORLD, Type.EXPRESSION, "int", false, false, false, "getWorld().getWidth()","entity");
        definitions.add(bd);
        
        bd = new BloxsDefinition("World", "GetHeight", "height of world", "", BloxsColors.$WORLD, Type.EXPRESSION, "int", false, false, false, "getWorld().getHeight()","entity");
        definitions.add(bd);
        
        bd = new BloxsDefinition("World", "GetWidth", "width of world", "", BloxsColors.$WORLD, Type.EXPRESSION, "int", false, false, false, "getWidth()","world");
        definitions.add(bd);
        
        bd = new BloxsDefinition("World", "GetHeight", "height of world", "", BloxsColors.$WORLD, Type.EXPRESSION, "int", false, false, false, "getHeight()","world");
        definitions.add(bd);
        
        bd = new BloxsDefinition("World", "SetWidth", "set width of world $", "int", BloxsColors.$WORLD, Type.INSTRUCTION, "int", true, false, true, "setWidth($0);","world");
        definitions.add(bd);
        
        bd = new BloxsDefinition("World", "SetHeight", "set height of world $", "int", BloxsColors.$WORLD, Type.INSTRUCTION, "int", true, false, true, "setHeight($0);","world");
        definitions.add(bd);
        
        bd = new BloxsDefinition("World", "SetDimension", "set dimension of world width= $ height= $", "int,int", BloxsColors.$WORLD, Type.INSTRUCTION, "int", true, false, true, "setDimension($0,$1);","world");
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

        
        catColors.put("Graphical", BloxsColors.$GRAPHICAL);
        
        bd = new BloxsDefinition("Graphical", "LoadImage", "load image €", "Image", BloxsColors.$GRAPHICAL, Type.INSTRUCTION, "", true, false, true, "loadImage(\"$0\");","world,entity");
        //bd.setList(0, new List("int", new String[] {"int","double","boolean","String","long","float"}));
        definitions.add(bd);
        
        bd = new BloxsDefinition("Graphical", "GetImageName", "name of loaded image", "", BloxsColors.$GRAPHICAL, Type.EXPRESSION, "String", false, false, false, "getFilename()","world,entity");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Graphical", "Repaint", "repaint", "", BloxsColors.$GRAPHICAL, Type.INSTRUCTION, "", true, false, true, "getWorld().repaint();","world,entity");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Graphical", "SetColor", "set color $", "Color", BloxsColors.$GRAPHICAL, Type.INSTRUCTION, "", true, false, true, "g.setColor($0);","world,entity");
        bd.setNeedsParent("OnDraw");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Graphical", "Color", "new RGB color with R=$ G=$ B=$", "int,int,int", BloxsColors.$GRAPHICAL, Type.EXPRESSION, "Color", true, false, true, "new Color($0,$1,$2)","world,entity");
        bd.setNeedsParent("OnDraw");
        definitions.add(bd);
        bd = new BloxsDefinition("Graphical", "Color", "new HEX color $", "String", BloxsColors.$GRAPHICAL, Type.EXPRESSION, "Color", true, false, true, "new Color((int) Long.parseLong($0, 16))","world,entity");
        bd.setNeedsParent("OnDraw");
        definitions.add(bd);

        bd = new BloxsDefinition("Graphical", "SetFontSize", "set the font size to $", "int", BloxsColors.$GRAPHICAL, Type.INSTRUCTION, "", true, false, true, "g.setFont(new Font(g.getFont().getName(), g.getFont().getStyle(), $0));","world,entity");
        bd.setNeedsParent("OnDraw");
        definitions.add(bd);

        bd = new BloxsDefinition("Graphical", "DrawString", "draw the text $ at position x=$ y=$", "String,int,int", BloxsColors.$GRAPHICAL, Type.INSTRUCTION, "", true, false, true, "g.drawString($0,$1,$2);","world,entity");
        //bd.setTransformation(0,"stringify");
        bd.setNeedsParent("OnDraw");
        definitions.add(bd);

        bd = new BloxsDefinition("Graphical", "DrawLine", "draw line between the point x1=$ y1=$ and x2=$ y2=$", "int,int,int,int", BloxsColors.$GRAPHICAL, Type.INSTRUCTION, "", true, false, true, "g.drawLine($0,$1,$2,$3);","world,entity");
        bd.setNeedsParent("OnDraw");
        definitions.add(bd);

        bd = new BloxsDefinition("Graphical", "DrawRect", "draw outlined rectangle at position x=$ y=$ with width=$ height=$", "int,int,int,int", BloxsColors.$GRAPHICAL, Type.INSTRUCTION, "", true, false, true, "g.drawRect($0,$1,$2,$3);","world,entity");
        bd.setNeedsParent("OnDraw");
        definitions.add(bd);

        bd = new BloxsDefinition("Graphical", "FillRect", "draw filled rectangle at position x=$ y=$ with width=$ height=$", "int,int,int,int", BloxsColors.$GRAPHICAL, Type.INSTRUCTION, "", true, false, true, "g.fillRect($0,$1,$2,$3);","world,entity");
        bd.setNeedsParent("OnDraw");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Graphical", "DrawOval", "draw outlined oval at position x=$ y=$ with width=$ height=$", "int,int,int,int", BloxsColors.$GRAPHICAL, Type.INSTRUCTION, "", true, false, true, "g.drawOval($0,$1,$2,$3);","world,entity");
        bd.setNeedsParent("OnDraw");
        definitions.add(bd);

        bd = new BloxsDefinition("Graphical", "FillOval", "draw filled oval at position x=$ y=$ with width=$ height=$", "int,int,int,int", BloxsColors.$GRAPHICAL, Type.INSTRUCTION, "", true, false, true, "g.fillOval($0,$1,$2,$3);","world,entity");
        bd.setNeedsParent("OnDraw");
        definitions.add(bd);
        
        
        
        catColors.put("Application", BloxsColors.$MAIN);
        
        bd = new BloxsDefinition("Application", "SetWindowSize", "set window size to $ x $ pixels", "int,int", BloxsColors.$MAIN, Type.INSTRUCTION, "", true, false, true, "setWindowSize($0, $1);","main");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Application", "SetTitle", "set application title §", "", BloxsColors.$MAIN, Type.INSTRUCTION, "", true, false, true, "setTitle(\"$0\");","main");
        bd.setTransformation(0,"stringify");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Application", "GetWidth", "width of application", "", BloxsColors.$WORLD, Type.EXPRESSION, "int", false, false, false, "getWidth()","main");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Application", "GetHeight", "height of application", "", BloxsColors.$WORLD, Type.EXPRESSION, "int", false, false, false, "getHeight()","main");
        definitions.add(bd);
        
        
        
        catColors.put("Timer", BloxsColors.$OTHER);
        
        bd = new BloxsDefinition("Timer", "StartTask", "execute once after $ milliseconds", "int", BloxsColors.$OTHER, Type.INSTRUCTION, "", true, true, true, "{\n    java.util.Timer t = new java.util.Timer();\n    addSchedule(t);\n    t.schedule(\n        new java.util.TimerTask() {\n            @Override\n            public void run() {\n        $$$\n            }\n        },\n        $0\n    );\n}\n","entity,world");
        definitions.add(bd);
        
        
        bd = new BloxsDefinition("Timer", "StartTimer", "repeat each $ milliseconds", "int", BloxsColors.$OTHER, Type.INSTRUCTION, "", true, true, true, "{\n	Timer t = new Timer($0, new ActionListener() {\n		@Override public void actionPerformed(ActionEvent ae)\n		{\n$$$\n			if(getWorld()!=null) getWorld().repaint();\n			if(!$this.getWorld().isDisplayable())\n			{\n				((Timer)ae.getSource()).stop();\n			}\n		}\n	});\n	t.start();\n	addTimer(t);\n}\n","entity,world");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Timer", "StopTimer", "stop timer", "", BloxsColors.$OTHER, Type.INSTRUCTION, "", true, false, true, "((Timer)ae.getSource()).stop();","entity,world");
        bd.setNeedsParent("StartTimer");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Timer", "SetTimerDelay", "set delay $", "int", BloxsColors.$OTHER, Type.INSTRUCTION, "", true, false, true, "((Timer)ae.getSource()).setDelay($0);","entity,world");
        bd.setNeedsParent("StartTimer");
        definitions.add(bd);
        
        
        
        catColors.put("Mouse", BloxsColors.$MOUSE);
       
        bd = new BloxsDefinition("Mouse", "GetMouseX", "get mouse x", "int", BloxsColors.$MOUSE, Type.EXPRESSION, "", false, false, false, "getMouseX()","entity,world");
        definitions.add(bd);
       
        bd = new BloxsDefinition("Mouse", "GetMouseY", "get mouse y", "int", BloxsColors.$MOUSE, Type.EXPRESSION, "", false, false, false, "getMouseY()","entity,world");
        definitions.add(bd);
       
        bd = new BloxsDefinition("Mouse", "IsMouseInside", "mouse inside?", "", BloxsColors.$MOUSE, Type.CONDITION, "", false, false, false, "mouseInside()","entity");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Mouse", "HidePointer", "hide pointer", "", BloxsColors.$MOUSE, Type.INSTRUCTION, "", true, false, true, "hideCursor();","world");
        definitions.add(bd);
        
        bd = new BloxsDefinition("Mouse", "ShowPointer", "show pointer", "", BloxsColors.$MOUSE, Type.INSTRUCTION, "", true, false, true, "hideCursor();","world");
        definitions.add(bd);
        
        
        catColors.put("Debug", BloxsColors.$DEBUG);
       
        bd = new BloxsDefinition("Debug", "SystemOutPrintln", "print to console $", "", BloxsColors.$DEBUG, Type.INSTRUCTION, "", true, false, true, "System.out.println($0);","");
        definitions.add(bd);
       
        bd = new BloxsDefinition("Debug", "Comment", "comment §", "", BloxsColors.$DEBUG, Type.INSTRUCTION, "", true, false, true, "// $0","");
        definitions.add(bd);
       
        
        
       
    }
    
    public Color getColor(String categorie)
    {
        return catColors.get(categorie);
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
