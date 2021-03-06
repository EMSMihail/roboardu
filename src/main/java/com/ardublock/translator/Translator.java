package com.ardublock.translator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ardublock.translator.adaptor.BlockAdaptor;
import com.ardublock.translator.adaptor.OpenBlocksAdaptor;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.TranslatorBlockFactory;
import com.ardublock.translator.block.exception.BlockException;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNameDuplicatedException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

import com.mit.blocks.codeblocks.Block;
import com.mit.blocks.renderable.RenderableBlock;
import com.mit.blocks.workspace.Workspace;

public class Translator
{
	private static final String variablePrefix = "_";

	private Set<String> headerFileSet;
        private Set<String> headerDefinitionSet;
	private Set<String> definitionSet;
	private List<String> setupCommand;
	private List<String> guinoCommand;
	private Set<String> functionNameSet;
	private Set<TranslatorBlock> bodyTranslatreFinishCallbackSet;
	private BlockAdaptor blockAdaptor;
	
	private Set<String> inputPinSet;
	private Set<String> outputPinSet;
	
	private Map<String, String> numberVariableSet;
	private Map<String, String> booleanVariableSet;
	private Map<String, String> stringVariableSet;
	private Map<String, Object> internalData;
	
	private Workspace workspace;
	
	private String rootBlockName;
	
	private int variableCnt;
	private boolean isScoopProgram;
	private boolean isGuinoProgram;

	public Translator(Workspace ws)
	{
		workspace = ws;
		reset();
	}
	
	public String genreateHeaderCommand()
	{
		StringBuilder headerCommand = new StringBuilder();
		
		if (!headerFileSet.isEmpty())
		{
			for (String file:headerFileSet)
			{
				headerCommand.append("#include <" + file + ">\n");
			}
		}
		
                if (!headerDefinitionSet.isEmpty())
		{
			for (String command:headerDefinitionSet)
			{
				headerCommand.append(command + "\n");
			}
		}
                
		if (!definitionSet.isEmpty())
		{
			for (String command:definitionSet)
			{
				headerCommand.append(command + "\n");
			}
		}
		
		if (!functionNameSet.isEmpty())
		{
			for (String functionName:functionNameSet)
			{
				headerCommand.append("void " + functionName + "();\n");
			}
		}
		
		return headerCommand.toString() + "\n" + generateSetupFunction() +
                        generateGuinoFunction();
	}
	
	public String generateSetupFunction()
	{
		StringBuilder setupFunction = new StringBuilder();
		setupFunction.append("void setup()\n{\n");
		
		if (!inputPinSet.isEmpty())
		{
			for (String pinNumber:inputPinSet)
			{
				setupFunction.append("pinMode( " + pinNumber + " , INPUT);\n");
			}
		}
		if (!outputPinSet.isEmpty())
		{
			for (String pinNumber:outputPinSet)
			{
				setupFunction.append("pinMode( " + pinNumber + " , OUTPUT);\n");
			}
		}
		
		if (!setupCommand.isEmpty())
		{
			for (String command:setupCommand)
			{
				setupFunction.append(command + "\n");
			}
			
		}

		
		setupFunction.append("}\n\n");
		
		return setupFunction.toString();
	}
	
	public String generateGuinoFunction()
	{
		StringBuilder guinoFunction = new StringBuilder();
		
		
		if (!guinoCommand.isEmpty())
		{
			guinoFunction.append("void GUINO_DEFINIR_INTERFACE()\n{\n");
			for (String command:guinoCommand)
			{
				guinoFunction.append(command + "\n");
			}
			guinoFunction.append("}\n\n");
		}
		
		
		return guinoFunction.toString();
	}
	
	public String translate(Long blockId) throws SocketNullException, SubroutineNotDeclaredException, BlockException
	{
		TranslatorBlockFactory translatorBlockFactory = new TranslatorBlockFactory();
		Block block = workspace.getEnv().getBlock(blockId);
		TranslatorBlock rootTranslatorBlock = translatorBlockFactory.buildTranslatorBlock(this, blockId, block.getGenusName(), "", "", block.getBlockLabel());
		return rootTranslatorBlock.toCode();
	}
	
	public BlockAdaptor getBlockAdaptor()
	{
		return blockAdaptor;
	}
	
	public void reset()
	{
		headerFileSet = new LinkedHashSet<String>();
		headerDefinitionSet = new LinkedHashSet<String>();
                definitionSet = new LinkedHashSet<String>();
		setupCommand = new LinkedList<String>();
		guinoCommand = new LinkedList<String>();
		functionNameSet = new HashSet<String>();
		inputPinSet = new HashSet<String>();
		outputPinSet = new HashSet<String>();
		bodyTranslatreFinishCallbackSet = new HashSet<TranslatorBlock>();
		
		numberVariableSet = new HashMap<String, String>();
		booleanVariableSet = new HashMap<String, String>();
		stringVariableSet = new HashMap<String, String>();
		
		internalData =  new HashMap<String, Object>();
		blockAdaptor = buildOpenBlocksAdaptor();
		
		variableCnt = 0;
		
		rootBlockName = null;
		isScoopProgram = false;
		isGuinoProgram = false;
	}
	
	private BlockAdaptor buildOpenBlocksAdaptor()
	{
		return new OpenBlocksAdaptor();
	}
	
	public void addHeaderFile(String headerFile)
	{
		if (!headerFileSet.contains(headerFile))
		{
			headerFileSet.add(headerFile);
		}
	}
        
        public void addHeaderDefinition(String headerDefinition)
	{
		if (!headerDefinitionSet.contains(headerDefinition))
		{
			headerDefinitionSet.add(headerDefinition);
		}
	}
	
	public void addSetupCommand(String command)
	{
		if (!setupCommand.contains(command))
		{
			setupCommand.add(command);
		}
	}
	
	public void addSetupCommandForced(String command)
	{
		setupCommand.add(command);
	}
	
	public void addGuinoCommand(String command)
	{
		
			guinoCommand.add(command);
		
	}
	
	public void addDefinitionCommand(String command)
	{
		definitionSet.add(command);
	}
	
	public void addInputPin(String pinNumber)
	{
		inputPinSet.add(pinNumber);
	}
	
	public void addOutputPin(String pinNumber)
	{
		outputPinSet.add(pinNumber);
	}
	
	public String getNumberVariable(String userVarName)
	{
		return numberVariableSet.get(userVarName);
	}
	
	public String getBooleanVariable(String userVarName)
	{
		return booleanVariableSet.get(userVarName);
	}

	public void addNumberVariable(String userVarName, String internalName)
	{
		numberVariableSet.put(userVarName, internalName);
	}
	
	public void addBooleanVariable(String userVarName, String internalName)
	{
		booleanVariableSet.put(userVarName, internalName);
	}

	public void addFunctionName(Long blockId, String functionName) throws SubroutineNameDuplicatedException
	{
		if (functionName.equals("loop") ||functionName.equals("setup") || functionNameSet.contains(functionName))
		{
			throw new SubroutineNameDuplicatedException(blockId);
		}
		
		functionNameSet.add(functionName);
	}
	
	public boolean containFunctionName(String name)
	{
		return functionNameSet.contains(name.trim());
	}
	
	
	public String buildVariableName()
	{
		return buildVariableName("");
	}
	
	public String buildVariableName(String reference)
	{
		variableCnt = variableCnt + 1;
		String varName = variablePrefix + variableCnt + "_";
		int i;
		for (i=0; i<reference.length(); ++i)
		{
			char c = reference.charAt(i);
			if (Character.isLetter(c) || Character.isDigit(c) || (c == '_'))
			{
				varName = varName + c;
			}
		}
		return varName;
	}
	
	public Workspace getWorkspace() {
		return workspace;
	}
	
	public Block getBlock(Long blockId) {
		return workspace.getEnv().getBlock(blockId);
	}
	
	public void registerBodyTranslateFinishCallback(TranslatorBlock translatorBlock)
	{
		bodyTranslatreFinishCallbackSet.add(translatorBlock);
	}

	public void beforeGenerateHeader()  throws SocketNullException, SubroutineNotDeclaredException
	{
		for (TranslatorBlock translatorBlock : bodyTranslatreFinishCallbackSet)
		{
			translatorBlock.onTranslateBodyFinished();
		}
	}

	public String getRootBlockName() {
		return rootBlockName;
	}

	public void setRootBlockName(String rootBlockName) {
		this.rootBlockName = rootBlockName;
	}

	public boolean isScoopProgram() {
		return isScoopProgram;
	}

	public void setScoopProgram(boolean isScoopProgram) {
		this.isScoopProgram = isScoopProgram;
	}

	public boolean isGuinoProgram() {
		return isGuinoProgram;
	}

	public void setGuinoProgram(boolean isGuinoProgram) {
		this.isGuinoProgram = isGuinoProgram;
	}

	public String translate(Set<RenderableBlock> loopBlocks, Set<RenderableBlock> subroutineBlocks) throws SocketNullException, SubroutineNotDeclaredException
	{
		StringBuilder code = new StringBuilder();
		
		for (RenderableBlock renderableBlock : loopBlocks)
		{
			Block loopBlock = renderableBlock.getBlock();
			code.append(translate(loopBlock.getBlockID()));
		}
		
		for (RenderableBlock renderableBlock : subroutineBlocks)
		{
			Block subroutineBlock = renderableBlock.getBlock();
			code.append(translate(subroutineBlock.getBlockID()));
		}
		beforeGenerateHeader();
		code.insert(0, genreateHeaderCommand());
		
		return code.toString();
	}
	
	public Object getInternalData(String name)
	{
		return internalData.get(name);
	}
	
	public void addInternalData(String name, Object value)
	{
		internalData.put(name, value);
	}
}
