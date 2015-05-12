package whilelang.io;

import jasm.*;
import jasm.attributes.Code;
import jasm.lang.Bytecode;
import jasm.lang.ClassFile;
import jasm.lang.JvmType;
import jasm.lang.JvmTypes;
import jasm.lang.Modifier;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;

import whilelang.lang.Stmt;
import whilelang.lang.WhileFile;
import whilelang.lang.WhileFile.Decl;
import whilelang.lang.WhileFile.FunDecl;

/**
 * Responsible for translating a While source file into a JVM Class file.
 * 
 * @author David J. Pearce
 * 
 */
public class ClassFileWriter {
	
	 jasm.io.ClassFileWriter writer;
	public	static JvmType.Clazz JAVA_LANG_SYSTEM = new JvmType.Clazz("java.lang", "System");
	public 	static JvmType.Clazz JAVA_IO_PRINTSTREAM = new JvmType.Clazz("java.io", "PrintStream");
	private static ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
	private static ArrayList<Bytecode> bytecodes = new ArrayList<Bytecode>();
	
	private static HashMap<String, WhileFile.Decl> declarations = new HashMap<String, WhileFile.Decl>();
	private static HashMap<String, WhileFile.Decl> functions = new HashMap<String, WhileFile.Decl>();
	
	private static int slot = 0;
	public ClassFileWriter(File classFile) throws FileNotFoundException {
		 writer = new jasm.io.ClassFileWriter(new FileOutputStream(classFile));
	}
	
	public void write(WhileFile sourceFile) throws IOException {
		String className = sourceFile.filename.substring(0,sourceFile.filename.indexOf('.') );
		modifiers.add(Modifier.ACC_PUBLIC);
		
		jasm.lang.ClassFile cf = new ClassFile(
					49,                                 // Java 1.5 or later
					new JvmType.Clazz("",className), // class is HelloWorld
					JvmTypes.JAVA_LANG_OBJECT,          // superclass is Object
					Collections.EMPTY_LIST,             // implements no interfaces
					modifiers);                         // which is public


		// TODO: implement this method!! 
		 for (WhileFile.Decl decl : sourceFile.declarations) {
			 declarations.put(decl.name(), decl);
		}
		 
		 WhileFile.Decl main = declarations.get("main");
			if(main instanceof WhileFile.FunDecl) {
				WhileFile.FunDecl fd = (WhileFile.FunDecl) main;
				createMainMethod(cf, fd);
			} else {
				System.out.println("Cannot find a main() function");
			}
//			if(fd instanceof WhileFile.FunDecl){
//			if (fd.name().equals("main")) {
//				WhileFile.FunDecl dec = (FunDecl) fd;
//				createMainMethod(cf, dec);
//			}
//			else{
//				createMethod(cf);
//			}
//		}
		 writer.write(cf);
	}
	


	private void createMainMethod(ClassFile cf, WhileFile.FunDecl fd){
		modifiers.add(Modifier.ACC_STATIC);
		ClassFile.Method method = new ClassFile.Method(
				"main",                                              // main method
				new JvmType.Function(                                // is function
				JvmTypes.T_VOID,                             // from void
				new JvmType.Array(JvmTypes.JAVA_LANG_STRING)), // to array of String
				modifiers); // which is static public
		for (Stmt stmt : fd.statements) {
			processStatement(stmt);
		}
		
		bytecodes.add(new Bytecode.Return(null));
		
		method.attributes().add(new Code(bytecodes, Collections.EMPTY_LIST, method));		
		cf.methods().add(method);
	}
	
	private void createMethod(ClassFile cf) {
		// TODO Auto-generated method stub
		
	}

	private void processStatement(Stmt stmt){
		if(stmt instanceof Stmt.Print){
			processStatement((Stmt.Print) stmt);
		}
		else if(stmt instanceof Stmt.Assign){
			processStatement((Stmt.Assign) stmt);
		}
		else if(stmt instanceof Stmt.For){
			processStatement((Stmt.For) stmt);
		}
		else if(stmt instanceof Stmt.IfElse){
			processStatement((Stmt.IfElse) stmt);
		}
		else if(stmt instanceof Stmt.Return){
			processStatement((Stmt.Return) stmt);
		}
		else if(stmt instanceof Stmt.While){
			processStatement((Stmt.While) stmt);
		}
		else if(stmt instanceof Stmt.VariableDeclaration){
			processStatement((Stmt.VariableDeclaration) stmt);
		}
	}
	
	private void processStatement(Stmt.Print stmt){
		bytecodes.add(new Bytecode.GetField(JAVA_LANG_SYSTEM, "out",
						JAVA_IO_PRINTSTREAM,
						Bytecode.FieldMode.STATIC));
		
		bytecodes.add(new Bytecode.LoadConst("sausage"));
		
		bytecodes.add(new Bytecode.Invoke(JAVA_IO_PRINTSTREAM, "println",
						new JvmType.Function(JvmTypes.T_VOID,
						JvmTypes.JAVA_LANG_STRING),
						Bytecode.InvokeMode.VIRTUAL));
		
		incrementSlot();
	}
	private void processStatement(Stmt.Assign stmt){ 
		bytecodes.add(new Bytecode.Load(slot, null));
		incrementSlot();
	}
	private void processStatement(Stmt.For stmt){
		incrementSlot();
	}
	private void processStatement(Stmt.IfElse stmt){
		incrementSlot();
	}
	private void processStatement(Stmt.Return stmt){
		incrementSlot();
	}
	private void processStatement(Stmt.While stmt){
		incrementSlot();
	}
	private void processStatement(Stmt.VariableDeclaration stmt){	
		bytecodes.add(new Bytecode.Store(slot, JvmTypes.JAVA_LANG_BOOLEAN));
		
		incrementSlot();
	}
	
	private static void incrementSlot(){
		slot++;
	}
}
