package jdtpluign.handlers;

import java.awt.print.Printable;
import java.lang.reflect.Method;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.resources.IWorkspace.ProjectOrder;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.core.search.matching.MatchLocatorParser.ClassAndMethodDeclarationVisitor;
import org.eclipse.jdt.launching.sourcelookup.containers.JavaProjectSourceContainer;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.service.resolver.ImportPackageSpecification;

public class SampleHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// Get workspace object
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		// get the root of the workspace
		
		IWorkspaceRoot workspaceRoot = workspace.getRoot();
		
		IProject[] projects = workspaceRoot.getProjects();
		
		//1. Project is open
		//2. Project has java nature (is a java project);
		
		for (IProject project: projects) {
			try {
				if (project.isOpen() && project.hasNature(JavaCore.NATURE_ID)) {
					System.out.println("Project:" + project.getName());
					IJavaProject javaProject = JavaCore.create(project);
					
					analyzeJavaProject(javaProject);
				}
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	private void analyzeJavaProject(IJavaProject javaProject) {
		// Package level
		try {
			for (IPackageFragment pkg: javaProject.getPackageFragments()) {
				// Choose only source packages (exclude libs)
				if (pkg.getKind() == IPackageFragmentRoot.K_SOURCE) {
					System.out.println("Package:" + pkg.getElementName());
					
					// Analyze compilation units
					analyzePackage(pkg);
				}
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void analyzePackage(IPackageFragment pkg) {
		//Iterate compilation units in a package
		
		try {
			for (ICompilationUnit unit: pkg.getCompilationUnits()) {
				analyzeCompilationUnit(unit);
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void analyzeCompilationUnit(ICompilationUnit unit) {
		System.out.println("Compilation unit" + unit.getElementName());
		
		// Build AST
		ASTParser parser =  ASTParser.newParser(AST.JLS22); 
		parser.setSource(unit);
		parser.setKind(ASTParser.K_COMPILATION_UNIT); // set generate compilation Unit level for AST
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true); // recovery for incomplete code
		parser.setStatementsRecovery(true); // recovery for code with syntax error
		
		// Build AST for current compilation unit
		CompilationUnit astRoot = (CompilationUnit) parser.createAST(null);
		
//		MethodDeclarationVisitor visitor = new MethodDeclarationVisitor();
//		CatchVisitor visitor = new CatchVisitor();
		TryVisitor visitor = new TryVisitor();
		astRoot.accept(visitor);
//		System.out.println(visitor.getCatchCount());
		
		
//		System.out.println("Number of methods visitor:" + Integer.toString(visitor.getMethodCount()));
	}
	
}
