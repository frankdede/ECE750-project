package jdtplugin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class SampleHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// Get workspace object
		IWorkspace workspace = ResourcesPlugin.getWorkspace();		
		IWorkspaceRoot workspaceRoot = workspace.getRoot();
		
		
		IProject[] projects = null;
		
        String projectName = System.getProperty("projectName"); // VM argument method

        
        if (projectName == null) {
        	projects = workspaceRoot.getProjects();
        } else {
        	projects = new IProject[] {workspaceRoot.getProject(projectName)};
        }
        
        
		for (IProject project: projects) {
			
			try {
				if (project.isOpen() && project.hasNature(JavaCore.NATURE_ID)) {
					System.out.println("Project:" + project.getName());
					IJavaProject javaProject = JavaCore.create(project);
					
					analyzeJavaProject(javaProject);
				}
			} catch (CoreException e) {
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
			if (pkg.getCompilationUnits().length > 0) {
				System.out.println("Package:" + pkg.getElementName());
			}

			for (ICompilationUnit unit: pkg.getCompilationUnits()) {
				analyzeCompilationUnit(unit);
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void analyzeCompilationUnit(ICompilationUnit unit) throws JavaModelException {
		System.out.println("Compilation unit: " + unit.getElementName());
		
		// Build AST
		ASTParser parser =  ASTParser.newParser(AST.JLS22); 
		
		String sourceCode = unit.getSource();
		parser.setSource(unit);
		parser.setKind(ASTParser.K_COMPILATION_UNIT); // set generate compilation Unit level for AST
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true); // recovery for incomplete code
		parser.setStatementsRecovery(true); // recovery for code with syntax error
		
		// Build AST for current compilation unit
		CompilationUnit astRoot = (CompilationUnit) parser.createAST(null);
		
		MultilineLogVisitor multiLineLogVisitor = new MultilineLogVisitor(sourceCode);
		ThrowInFinallyVisitor throwInFinallyVisitor = new ThrowInFinallyVisitor(sourceCode);
		GetCauseInCatchVisitor getCauseInCatchVisitor = new GetCauseInCatchVisitor(sourceCode);
		DummyCatchVisitor dummyCatchVisitor = new DummyCatchVisitor(sourceCode);

		
		System.out.println("===== Started analyzing =====");
		astRoot.accept(multiLineLogVisitor);
		astRoot.accept(throwInFinallyVisitor);
		astRoot.accept(getCauseInCatchVisitor);
		astRoot.accept(dummyCatchVisitor);
		System.out.println("***** Completed analyzing *****");


	}
	
}
