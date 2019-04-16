package com.ardublock.translator.block.roboarduBlock.Engine;

import java.util.List;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;


public class Engine_BackTime extends TranslatorBlock
{
	private List<String> setupCommand;
	
	public Engine_BackTime(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{   
            super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
        private static final String MOTORS_DEFINE_PIN = "" +
                "#define M_DIR_PIN_1               4\n" +
                "#define M_DIR_PIN_2               7\n" +
                "#define M_SPEED_PIN_1             5\n" +
                "#define M_SPEED_PIN_2             6\n";
        private static final String MOTORS_DEFINE_VAR = "" + 
                "int SpeedMotor1, SpeedMotor2;\n";
        private static final String MOTORS_DEFINE = "" +
                "void InitMotors()\n" +
                "{\n" +
                "  pinMode(M_DIR_PIN_1, OUTPUT);\n" +
                "  pinMode(M_DIR_PIN_2, OUTPUT);\n" +
                "  pinMode(M_SPEED_PIN_1, OUTPUT);\n" +
                "  pinMode(M_SPEED_PIN_2, OUTPUT);\n" +
                "\n" +
                "  SpeedMotor1 = 0;\n" +
                "  SpeedMotor2 = 0;\n" +
                "}\n" +
                "\n" +
                "void Motor1(int Speed)\n" +
                "{\n" +
                "  int Dir = 0;\n" +
                "\n" +
                "  if(Speed > 100)  Speed = 100;\n" +
                "  if(Speed < -100)  Speed = -100;\n" +
                "\n" +
                "  map(Speed,-100,100,-255,255);\n" +
                "  if(Speed < 0)\n" +
                "  {\n" +
                "    Dir = 1;\n" +
                "    Speed *= -1;\n" +
                "  }\n" +
                "\n" +
                "  digitalWrite(M_DIR_PIN_1, Dir);\n" +
                "  analogWrite(M_SPEED_PIN_1, Speed);\n" +
                "\n" +
                "  SpeedMotor1 = Speed;\n" +
                "}\n" +
                "\n" +
                "void Motor2(int Speed)\n" +
                "{\n" +
                "  int Dir = 0;\n" +
                "\n" +
                "  if(Speed > 100)  Speed = 100;\n" +
                "  if(Speed < -100)  Speed = -100;\n" +
                "\n" +
                "  map(Speed,-100,100,-255,255);\n" +
                "  if(Speed < 0)\n" +
                "  {\n" +
                "    Dir = 1;\n" +
                "    Speed *= -1;\n" +
                "  }\n" +
                "\n" +
                "  digitalWrite(M_DIR_PIN_2, Dir);\n" +
                "  analogWrite(M_SPEED_PIN_2, Speed);\n" +
                "\n" +
                "  SpeedMotor2 = Speed;\n" +
                "}\n" +
                "\n" +
                "void Motors(int Speed1, int Speed2)\n" +
                "{\n" +
                "  Motor1(Speed1);\n" +
                "  Motor2(Speed2);\n" +
                "}\n";
        private static final String MOTORS_BACK_DEFINE = "" +
                "void MotorsBack(int Speed)\n" +
                "{\n" +
                "  Motors(-Speed, -Speed);\n" +
                "}\n";
        private static final String MOTORS_STOP_DEFINE = "" +
                "void MotorsStop()\n" +
                "{\n" +
                "  Motors(0, 0);\n" +
                "}\n";
        private static final String MOTORS_BACK_TIME_DEFINE = "" +
                "void MotorsBackTime(int Speed, int Time)\n" +
                "{\n" +
                "  MotorsBack(Speed);\n" +
                "  delay(Time);\n" +
                "  MotorsStop();\n" +
                "}\n";
        
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
            translator.addHeaderDefinition(MOTORS_DEFINE_PIN);
            translator.addHeaderDefinition(MOTORS_DEFINE_VAR);
            translator.addDefinitionCommand(MOTORS_DEFINE);
            translator.addDefinitionCommand(MOTORS_BACK_DEFINE);
            translator.addDefinitionCommand(MOTORS_STOP_DEFINE);
            translator.addDefinitionCommand(MOTORS_BACK_TIME_DEFINE);
            translator.addSetupCommand("InitMotors();");
            TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
            String ret = "MotorsBackTime(" + translatorBlock.toCode() + ", ";
            translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
            ret = ret + translatorBlock.toCode() + " );";
            return codePrefix + ret + codeSuffix;
        }
}