/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.hacklab.mikkeli.logisim.switches;

import com.cburch.logisim.tools.AddTool;
import com.cburch.logisim.tools.Library;
import com.cburch.logisim.tools.Tool;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Ilmo Euro <ilmo.euro@gmail.com>
 */
public class Switches extends Library {

    private final List<AddTool> tools = Arrays.asList(
            new AddTool(new SinglePoleSingleThrow()),
            new AddTool(new SinglePoleDoubleThrow1to2())
    );

    @Override
    public List<? extends Tool> getTools() {
        return tools;
    }

    @Override
    public String getDisplayName() {
        return "Switches";
    }
}
