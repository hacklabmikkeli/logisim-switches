/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.hacklab.mikkeli.logisim.switches;

import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.Attributes;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.instance.Instance;
import com.cburch.logisim.instance.InstanceData;
import com.cburch.logisim.instance.InstanceDataSingleton;
import com.cburch.logisim.instance.InstanceFactory;
import com.cburch.logisim.instance.InstancePainter;
import com.cburch.logisim.instance.InstancePoker;
import com.cburch.logisim.instance.InstanceState;
import com.cburch.logisim.instance.Port;
import com.cburch.logisim.instance.StdAttr;
import com.cburch.logisim.util.GraphicsUtil;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

/**
 *
 * @author Ilmo Euro <ilmo.euro@gmail.com>
 */
public abstract class SwitchBase extends InstanceFactory {

    protected static final int SIZE = 20;
    protected static final Attribute<?> TYPE = Attributes.forOption("Type", SwitchType.values());
    protected static final int ARROW_SIZE = 3;
    protected static final Color ARROW_COLOR = Color.GRAY;

    protected static void applyActiveState(InstanceState is) {
        if (is.getAttributeValue(TYPE) == SwitchType.Latching) {
            SwitchState value = getValue(is);
            if (value == SwitchState.Open) {
                value = SwitchState.Closed;
            } else {
                value = SwitchState.Open;
            }
            setValue(is, value);
        } else if (is.getAttributeValue(TYPE) == SwitchType.NormallyOpen) {
            setValue(is, SwitchState.Closed);
        } else {
            setValue(is, SwitchState.Open);
        }
    }

    protected static void applyInactiveState(InstanceState is) {
        if (is.getAttributeValue(TYPE) == SwitchType.NormallyOpen) {
            setValue(is, SwitchState.Open);
        } else if (is.getAttributeValue(TYPE) == SwitchType.NormallyClosed) {
            setValue(is, SwitchState.Closed);
        }
    }

    protected static <T> void setValue(InstanceState state, T val) {
        InstanceDataSingleton data = (InstanceDataSingleton) state.getData();
        if (data == null) {
            state.setData(new InstanceDataSingleton(val));
        } else {
            data.setValue(val);
        }
        state.getInstance().fireInvalidated();
    }

    @SuppressWarnings(value = "unchecked")
    protected static <T> T getValue(InstanceState state) {
        InstanceDataSingleton data = (InstanceDataSingleton) state.getData();
        if (data == null) {
            return null;
        } else {
            return (T) data.getValue();
        }
    }

    @SuppressWarnings(value = "unchecked")
    protected static <T> T getValue(InstanceData instanceData) {
        InstanceDataSingleton data = (InstanceDataSingleton) instanceData;
        if (data == null) {
            return null;
        } else {
            return (T) data.getValue();
        }
    }

    public SwitchBase(String name) {
        super(name);
    }

    protected void moveTextField(Instance instance) {
        Object facing = instance.getAttributeValue(StdAttr.FACING);
        Bounds bds = instance.getBounds();
        int x = bds.getX() + bds.getWidth() / 2;
        int y = bds.getY() + bds.getHeight() / 2;
        int halign = GraphicsUtil.H_CENTER;
        int valign = GraphicsUtil.V_CENTER;
        if (facing == Direction.EAST) {
            y = bds.getY() - 2;
            valign = GraphicsUtil.V_BOTTOM;
        } else if (facing == Direction.WEST) {
            y = bds.getY() + bds.getHeight() + 2;
            valign = GraphicsUtil.V_TOP;
        } else if (facing == Direction.NORTH) {
            x = bds.getX() + bds.getWidth() + 2;
            halign = GraphicsUtil.H_LEFT;
        } else if (facing == Direction.SOUTH) {
            x = bds.getX() - 2;
            halign = GraphicsUtil.H_RIGHT;
        }
        instance.setTextField(StdAttr.LABEL, StdAttr.LABEL_FONT, x, y, halign, valign);
    }

    protected abstract Port[] portsFor(Direction facing);

    protected void movePorts(Instance instance) {
        Direction facing = instance.getAttributeValue(StdAttr.FACING);
        instance.setPorts(portsFor(facing));
    }

    @Override
    protected void configureNewInstance(Instance instance) {
        instance.addAttributeListener();
        moveTextField(instance);
        movePorts(instance);
    }

    @Override
    protected void instanceAttributeChanged(Instance instance, Attribute<?> attr) {
        if (attr == StdAttr.FACING) {
            instance.recomputeBounds();
            moveTextField(instance);
            movePorts(instance);
        }
    }

    protected void drawArrow(Graphics g, Object facing, int xc, int yc) {
        Color origColor = g.getColor();
        g.setColor(ARROW_COLOR);
        if (facing == Direction.EAST) {
            g.drawLine(xc - ARROW_SIZE, yc + ARROW_SIZE, xc + ARROW_SIZE, yc + ARROW_SIZE * 2);
            g.drawLine(xc + ARROW_SIZE, yc + ARROW_SIZE * 2, xc - ARROW_SIZE, yc + ARROW_SIZE * 3);
        }
        if (facing == Direction.WEST) {
            g.drawLine(xc + ARROW_SIZE, yc + ARROW_SIZE, xc - ARROW_SIZE, yc + ARROW_SIZE * 2);
            g.drawLine(xc - ARROW_SIZE, yc + ARROW_SIZE * 2, xc + ARROW_SIZE, yc + ARROW_SIZE * 3);
        }
        if (facing == Direction.NORTH) {
            g.drawLine(xc - ARROW_SIZE * 2, yc - ARROW_SIZE, xc - ARROW_SIZE, yc + ARROW_SIZE);
            g.drawLine(xc - ARROW_SIZE * 2, yc - ARROW_SIZE, xc - ARROW_SIZE * 3, yc + ARROW_SIZE);
        }
        if (facing == Direction.SOUTH) {
            g.drawLine(xc - ARROW_SIZE * 2, yc + ARROW_SIZE, xc - ARROW_SIZE, yc - ARROW_SIZE);
            g.drawLine(xc - ARROW_SIZE * 2, yc + ARROW_SIZE, xc - ARROW_SIZE * 3, yc - ARROW_SIZE);
        }
        g.setColor(origColor);
    }

    protected void drawArm(InstancePainter ip,
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
            if (facing == Direction.EAST || facing == Direction.WEST) {
                g.drawLine(x, yc, x + w, yc);
            } else {
                g.drawLine(xc, y, xc, y + h);
            }
        } else if (facing == Direction.EAST || facing == Direction.WEST) {
            g.drawLine(x, yc, x + w, y);
        } else {
            g.drawLine(xc, y, x + w, y + h);
        }
    }

    public static class SwitchPoker extends InstancePoker {

        @Override
        public void mousePressed(InstanceState state, MouseEvent e) {
            applyActiveState(state);
        }

        @Override
        public void mouseReleased(InstanceState state, MouseEvent e) {
            applyInactiveState(state);
        }
    }
}
