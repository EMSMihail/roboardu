package com.ardublock.translator.block.roboarduBlock.display;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;

public class DisplayShow extends TranslatorBlock {
    public DisplayShow (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
    {
        super(blockId, translator, codePrefix, codeSuffix, label);
    }


    @Override
    public String toCode()
    {
        String display="display";
        translator.addHeaderFile("Wire.h");
        translator.addHeaderFile("Adafruit_GFX.h");
        translator.addHeaderFile("Adafruit_SSD1306.h");
        translator.addDefinitionCommand("Adafruit_SSD1306 "+display+"(128, 64, &Wire, -1);");
        translator.addSetupCommand("while(!" + display + ".begin(SSD1306_SWITCHCAPVCC, 0x3D) delay(1);\n" +
                display + ".display();\n" +
                "  delay(2000);\n" +
                "\n" +
                "  // Clear the buffer\n" +
                display + ".clearDisplay();" +
                display + ".display();\n");


        return codePrefix + display + ".display();\n" + codeSuffix;
    }

}
