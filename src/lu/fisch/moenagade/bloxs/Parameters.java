/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lu.fisch.moenagade.bloxs;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import lu.fisch.graphics.ColorUtils;
import static lu.fisch.moenagade.bloxs.Element.PADDING_LR;
import static lu.fisch.moenagade.bloxs.Element.fontsize;
import lu.fisch.moenagade.model.BloxsDefinition;

/**
 *
 * @author robert.fisch
 */
public class Parameters extends Element {

    public final static String CONFIG_SYMBOL = "\u267B";
    
    private boolean allowConfig = true;
    
    public Parameters() {
        init();
        setTitle(CONFIG_SYMBOL);
    }
    
    public Parameters(String returnType) {
        init();
        setReturnType(returnType);
        setTitle(CONFIG_SYMBOL);
        
    }

    public Parameters(BloxsDefinition bd) {
        super(bd);
        init();
    }
    
    public Parameters(String title, String[] items) {
        super(title);
        init();

        for (int i = 0; i < items.length; i++) {
            String text = items[i];
            Item item = new Item(text);
            item.setParent(this);
            addToBody(item);
        }
    }
    
    @Override
    public Parameters clone()
    {
        Parameters element = (Parameters) super.clone();
        element.allowConfig = this.allowConfig;
        
        return element;
    }

    public boolean allowConfig() {
        return allowConfig;
    }

    public void setAllowConfig(boolean allowConfig) {
        if(allowConfig)
        {
            if(!title.startsWith(CONFIG_SYMBOL))
                title=CONFIG_SYMBOL+" "+title.trim();
        }
        else
            title=title.replace(CONFIG_SYMBOL, "").trim();
        
        this.allowConfig = allowConfig;
        
        setTitle(title);
    }

    @Override
    public Element addParameter(Element parameterHolder) {
        if(parameterHolder.getType()==Type.EXPRESSION)
            title+=" $";
        else if(parameterHolder.getType()==Type.CONDITION)
            title+=" £";
        else if(parameterHolder.getType()==Type.VALUE)
            title+=" °";
        parseTitle(title,false);
        return super.addParameter(parameterHolder); 
    }

    private void init()
    {
        color = ColorUtils.getColor("#a0a0a0");
        isHolder=true;
        setType(Type.PARAMETERS);
        setClassname("Parameters");
    }
    
    @Override
    public String getJavaCode(int indent) {
        String code = "";
        if(allowConfig)
            for (int i = 0; i < parameters.size(); i++) {
                Element param = parameters.get(i);
                code+=param.getBody().getReturnType()+" "+param.getBody().getTitle();
                if(i<parameters.size()-1) code+=", ";
            }
        
        return code;
    }

    public ArrayList<VariableDefinition> getDefinitions() {
        ArrayList<VariableDefinition> result = new ArrayList<>();
        
        for (int i = 0; i < parameters.size(); i++) {
            Element param = parameters.get(i);
            result.add(new VariableDefinition(param.getBody().getTitle(), param.getBody().getReturnType()));
        }
        
        return result;
    }

    public void setDefinitions(ArrayList<VariableDefinition> def)
    {
        // generate the title
        title = CONFIG_SYMBOL;
        if(!allowConfig) title="";
        for (int i = 0; i < def.size(); i++) {
            VariableDefinition vd = def.get(i);
            title+=" $";
        }
        // parse the title
        parseTitle(title);
        for (int i = 0; i < def.size(); i++) {
            // get definition
            VariableDefinition vd = def.get(i);
            // get corresponding parameter
            Element param = getParameter(i);
            // set the type
            param.setReturnType(vd.type);
            // create the containing element
            Element content = new Element(Type.EXPRESSION, "Parameter", vd.type);
            content.setTitle(vd.name);
            content.setCode(vd.name);
            param.setBody(content);
        }
    }
    
    @Override
    public Rectangle draw(Graphics2D g, Point offset)
    {
        Rectangle result = new Rectangle();
        this.offset=offset;
        
        // set font
        g.setFont(new Font("Monospaced", Font.BOLD, fontsize));
        headDim = getHeadDimension(g);
        //System.out.println("Width of "+title+" = "+headDim.width);
        result = new Rectangle(offset, new Dimension(headDim.width, getTotalHeight(g)));
        
        // draw box
        g.setColor(color);
        g.fillRoundRect(offset.x, offset.y+2, 
                        headDim.width, headDim.height, 
                        2,2);
        if(hasError)
            g.setColor(Element.ERROR_COLOR);
        else if(dockBody)
            g.setColor(ColorUtils.getHighlight(color));
        else
            g.setColor(ColorUtils.getShadow(color));
        g.drawRoundRect(offset.x, offset.y+2, 
                        headDim.width, headDim.height, 
                        0,0);
        
        // text
        int x = offset.x+PADDING_LR;
        if(titlePieces.size()>0)
        {
            for (int i = 0; i < titlePieces.size(); i++) {
                String piece = titlePieces.get(i);
                // draw piece
                if(!piece.trim().isEmpty())
                {
                    g.setColor(TEXT_COLOR);
                    g.setFont(new Font("Monospaced", Font.BOLD, fontsize));
                    int th = g.getFontMetrics().getAscent();
                    g.drawString(piece.trim(), x, offset.y+th+(headDim.height+CONNECTOR_HEIGHT-th)/2);
                    x+=g.getFontMetrics().stringWidth(piece.trim()+" ");
                }
                // draw parameter
                if(i<parameters.size())
                {
                    Element param = parameters.get(i);
                    //System.out.println("DRawing param: "+i+" )) "+param.getClass().getSimpleName()+" : "+param.getClassname()+" > "+param);
                    Dimension paramHead = param.getHeadDimension(g);
                    result=param.draw(g, new Point(x, offset.y+(headDim.height+CONNECTOR_HEIGHT-paramHead.height)/2)).union(result);
                    drawLast.addAll(param.getDrawLast());
                    g.setFont(new Font("Monospaced", Font.BOLD, fontsize));
                    x+=paramHead.width+g.getFontMetrics().stringWidth(" ");
                }
            }
        }
        else
        {
            g.setColor(TEXT_COLOR);
            g.setFont(new Font("Monospaced", Font.BOLD, fontsize));
            int th = g.getFontMetrics().getAscent();
            g.drawString(title, x, offset.y+th+(headDim.height+CONNECTOR_HEIGHT-th)/2);
        }
        
        return result;
    }
}
