package jdtplugin.handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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
        
        
        
        // Define the target directory path
        String homeDir = System.getProperty("user.home");

        
		for (IProject project: projects) {
	        OutputStreamWriter writer;
	        String filePath = homeDir + File.separator + project.getName() + "_result.csv";
	        
			try {
				writer = new OutputStreamWriter(new FileOutputStream(filePath));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				throw new RuntimeException(e);
			}
			
			
			try {
				if (project.isOpen() && project.hasNature(JavaCore.NATURE_ID)) {
					System.out.println("Project:" + project.getName());
					IJavaProject javaProject = JavaCore.create(project);
					
					analyzeJavaProject(javaProject, writer);
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
			
			
			try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

		return null;
	}
	
	private void analyzeJavaProject(IJavaProject javaProject, OutputStreamWriter writer) {
		// Package level
		try {
			for (IPackageFragment pkg: javaProject.getPackageFragments()) {
				// Choose only source packages (exclude libs)
				if (pkg.getKind() == IPackageFragmentRoot.K_SOURCE) {
					// Analyze compilation units
					analyzePackage(pkg, writer);
				}
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void analyzePackage(IPackageFragment pkg, OutputStreamWriter writer) {
		//Iterate compilation units in a package
		try {
			if (pkg.getCompilationUnits().length > 0) {
				System.out.println("Package:" + pkg.getElementName());
			}

			for (ICompilationUnit unit: pkg.getCompilationUnits()) {
				
				System.out.print("Compilation unit: " + unit.getElementName());

				if (isCompilationUnitInTestPackage(unit)) {
					System.out.println("(test, skipped)");
				} else {
					System.out.println("");
					analyzeCompilationUnit(unit, writer);
				}
				
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static boolean isCompilationUnitInTestPackage(ICompilationUnit icu) {
        try {
            IResource resource = icu.getCorrespondingResource();
            if (resource != null) {
                String path = resource.getFullPath().toString();
                return path.contains("/src/test/java/") || path.contains("/src/test/");
            }
        } catch (JavaModelException e) {
            e.printStackTrace();
        }
        return false;
    }
	
	private void analyzeCompilationUnit(ICompilationUnit unit, OutputStreamWriter writer) throws JavaModelException {
		
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
		
		MultilineLogVisitor multiLineLogVisitor = new MultilineLogVisitor();
		ThrowInFinallyVisitor throwInFinallyVisitor = new ThrowInFinallyVisitor();
		GetCauseInCatchVisitor getCauseInCatchVisitor = new GetCauseInCatchVisitor();
		DummyCatchVisitor dummyCatchVisitor = new DummyCatchVisitor();
		
		
		multiLineLogVisitor.setOutputStreamWriter(writer);
		throwInFinallyVisitor.setOutputStreamWriter(writer);
		getCauseInCatchVisitor.setOutputStreamWriter(writer);
		dummyCatchVisitor.setOutputStreamWriter(writer);
		
		System.out.println("===== Started parsing =====");
		astRoot.accept(multiLineLogVisitor);
		astRoot.accept(throwInFinallyVisitor);
		astRoot.accept(getCauseInCatchVisitor);
		astRoot.accept(dummyCatchVisitor);
		System.out.println("***** Completed parsing *****");
	}
	
}
