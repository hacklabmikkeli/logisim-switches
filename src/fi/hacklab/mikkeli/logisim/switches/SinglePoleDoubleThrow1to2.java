/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.hacklab.mikkeli.logisim.switches;

import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.instance.InstancePainter;
import com.cburch.logisim.instance.InstanceState;
import com.cburch.logisim.instance.Port;
import com.cburch.logisim.instance.StdAttr;
import java.awt.Graphics;

/**
 *
 * @author Ilmo Euro <ilmo.euro@gmail.com>
 */
public class SinglePoleDoubleThrow1to2 extends SwitchBase {

    public SinglePoleDoubleThrow1to2() {
        super("Single-Pole Double-Throw Switch (2 outputs)");
        
		setPorts(portsFor(Direction.EAST));
        setOffsetBounds(Bounds.create(-(SIZE/2), -(SIZE/2), SIZE, SIZE));
        setAttributes(
                new Attribute[] {
                    StdAttr.FACING,
                    TYPE,
                    StdAttr.LABEL,
                    StdAttr.LABEL_FONT,
                },
                new Object[] {
                    Direction.EAST,
                    SwitchType.NormallyOpen,
                    "",
                    StdAttr.DEFAULT_LABEL_FONT,
                }
        );
        setInstancePoker(SwitchPoker.class);
    }
    

    @Override
    public void paintInstance(InstancePainter ip) {
		Bounds b = ip.getBounds();
		Object facing = ip.getAttributeValue(StdAttr.FACING);
        Graphics g = ip.getGraphics();

        ip.drawLabel();
        ip.drawPort(0);
        ip.drawPort(1);
        ip.drawPort(2);

		int x = b.getX();
		int y = b.getY();
		int w = b.getWidth();
		int h = b.getHeight();
        int xc = x + (w/2);
        int yc = y + (h/2);

        drawDTArm(ip, facing, g, x, y, xc, yc, w, h);
        drawArrow(g, facing, xc, yc);
    }

    protected void drawDTArm(
            InstancePainter ip,
            Object facing,
            Graphics g,
            int x,
            int y,
            int xc,
            int yc,
            int w,
            int h) {
        SwitchState state = getValue(ip.getData());
        if (ip.getShowState() && state == SwitchState.Closed) {
            if (facing == Direction.EAST) {
                g.drawLine(x, yc, x + w, y + h);
            } else if (facing == Direction.WEST) {
                g.drawLine(x, y + h, x + w, yc);
            } else if (facing == Direction.NORTH) {
                g.drawLine(x, y, xc, y + h);
            } else {
                g.drawLine(xc, y, x, y + h);
            }
        } else {
            if (facing == Direction.EAST) {
                g.drawLine(x, yc, x + w, y);
            } else if (facing == Direction.WEST) {
                g.drawLine(x, y, x + w, yc);
            } else if (facing == Direction.NORTH) {
                g.drawLine(x + w, y, xc, y + h);
            } else {
                g.drawLine(xc, y, x + w, y + h);
            }
        }
    }
    

    @Override
    public void propagate(InstanceState is) {
        if (getValue(is) == null) {
            applyInactiveState(is);
        }
        
        if (getValue(is) == SwitchState.Closed) {
            is.setPort(1, Value.UNKNOWN, 1);
            is.setPort(2, is.getPort(0), 1);
        } else {
            is.setPort(1, is.getPort(0), 1);
            is.setPort(2, Value.UNKNOWN, 1);
        }
    }

    @Override
    protected final Port[] portsFor(Direction facing) {
        if (facing == Direction.EAST) {
            return new Port[] {
                new Port(-(SIZE/2), 0, Port.INPUT, 1),
                new Port(+(SIZE/2), -(SIZE/2), Port.OUTPUT, 1),
                new Port(+(SIZE/2), +(SIZE/2), Port.OUTPUT, 1),
            };
        } else if (facing == Direction.WEST) {
            return new Port[] {
                new Port(+(SIZE/2), 0, Port.INPUT, 1),
                new Port(-(SIZE/2), -(SIZE/2), Port.OUTPUT, 1),
                new Port(-(SIZE/2), +(SIZE/2), Port.OUTPUT, 1),
            };
        } else if (facing == Direction.NORTH) {
            return new Port[] {
                new Port(0, SIZE/2, Port.INPUT, 1),
                new Port(-(SIZE/2), -(SIZE/2), Port.OUTPUT, 1),
                new Port(+(SIZE/2), -(SIZE/2), Port.OUTPUT, 1),
            };
        } else {
            return new Port[] {
                new Port(0, -(SIZE/2), Port.INPUT, 1),
                new Port(-(SIZE/2), SIZE/2, Port.OUTPUT, 1),
                new Port(+(SIZE/2), SIZE/2, Port.OUTPUT, 1),
            };
        }
    }
    
}
