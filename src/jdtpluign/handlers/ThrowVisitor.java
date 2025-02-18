package jdtpluign.handlers;

import java.util.Stack;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThrowStatement;

public class ThrowVisitor extends ASTVisitor{
	Stack<ASTNode> callStack = new Stack<>();

	public ThrowVisitor(Statement stmt) {
		this.callStack.push(stmt);
	}
	
	
	@Override
	public boolean visit(ThrowStatement node) {
		// TODO Auto-generated method stub
		this.callStack.push(node);
		System.out.println("ThrowVisitor - throw statement in finally: " + node);
		return false;
	}
		
	@Override
    public boolean visit(MethodInvocation node) {
		this.callStack.push(node);

		System.out.println("ThrowVisitor - method call in finally: " + node);
		IMethodBinding binding = (IMethodBinding) node.getName().resolveBinding();
		ICompilationUnit unit = (ICompilationUnit) binding.getJavaElement().getAncestor( IJavaElement.COMPILATION_UNIT );
		
		if ( unit == null ) {
			return false;
		}
		
		ASTParser parser = ASTParser.newParser(AST.JLS22); 
		parser.setSource(unit);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		parser.setStatementsRecovery(true);
		
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		MethodDeclaration decl = (MethodDeclaration)cu.findDeclaringNode(binding.getKey());
		decl.accept(this);
		return false;
    }
}
