package jdtplugin.handlers;


import java.lang.annotation.ElementType;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;


public abstract class BaseVisitor<T extends BaseVisitor<T>> extends ASTVisitor{
	protected String sourceCode;
	
	public BaseVisitor(String sourceCode) {
		this.sourceCode = sourceCode;
	}
	
	protected abstract T createSubclassInstance(String sourceCode);
	
	
	public void visitBinding(IBinding binding) throws JavaModelException  {
		ASTParser parser =  ASTParser.newParser(AST.JLS22); 
		if (binding == null) {
			return;
		}
		
		IJavaElement element = binding.getJavaElement();

		if (element == null) {
			return;
		}
		
		ICompilationUnit unit =(ICompilationUnit)element.getAncestor(IJavaElement.COMPILATION_UNIT);
			
		if (unit == null) {
			return;
		}
		String cuSourceCode = unit.getSource();
			
		parser.setSource(unit);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		parser.setStatementsRecovery(true);
		
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		MethodDeclaration decl = (MethodDeclaration)cu.findDeclaringNode(binding.getKey());
		T visitor = createSubclassInstance(cuSourceCode);
		
		decl.accept(visitor);
	}
	
	@Override
    public boolean visit(MethodInvocation node) {
		IMethodBinding binding = (IMethodBinding) node.getName().resolveBinding();

		try {
			this.visitBinding(binding);
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
    }
	
	@Override
    public boolean visit(ConstructorInvocation node) {
		IMethodBinding binding = (IMethodBinding) node.resolveConstructorBinding();

		try {
			this.visitBinding(binding);
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}

