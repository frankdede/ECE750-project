package jdtplugin.handlers;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

public class TryCauseVisitor extends ASTVisitor {
	
    public boolean visit(CatchClause node) {
        // Get the body of the catch block
        Block catchBlock = node.getBody();

        // Iterate over the statements in the catch block
        for (Object statement : catchBlock.statements()) {
            if (statement instanceof ExpressionStatement) {
                ExpressionStatement exprStmt = (ExpressionStatement) statement;
                Expression expression = exprStmt.getExpression();

                // Check if the expression is a method invocation
                if (expression instanceof MethodInvocation) {
                    MethodInvocation methodInvocation = (MethodInvocation) expression;
                    // Check if the method is getCause
                    if (methodInvocation.getName().getIdentifier().equals("getCause")) {
                        System.out.println("Found getCause() in the catch block!");
                    }
                }
            }
        }
        return super.visit(node);
    }
    

    // Method to visit method invocations
    @Override
    public boolean visit(MethodInvocation node) {
        System.out.println("TryCauseVisitor - Visited method call: " + node);
        
        // Resolve method binding to get method details
        IMethodBinding binding = (IMethodBinding) node.getName().resolveBinding();
        if (binding == null) {
            return false;
        }

        // Get the source code of the method being invoked
        ICompilationUnit unit = (ICompilationUnit) binding.getJavaElement().getAncestor(IJavaElement.COMPILATION_UNIT);
        if (unit == null) {
            return false;
        }

        // Parse the method's source code
        ASTParser parser = ASTParser.newParser(AST.JLS22);
        parser.setSource(unit);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setResolveBindings(true);
        parser.setBindingsRecovery(true);
        parser.setStatementsRecovery(true);

        CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        // Find the method declaration in the parsed compilation unit
        MethodDeclaration decl = (MethodDeclaration) cu.findDeclaringNode(binding.getKey());
        if (decl != null) {
            decl.accept(this); // Recursively visit the method declaration
        }

        return false; // Return false to allow normal AST traversal
    }

    
}
