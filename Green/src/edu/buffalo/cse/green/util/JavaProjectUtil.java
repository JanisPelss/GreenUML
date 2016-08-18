package edu.buffalo.cse.green.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

public class JavaProjectUtil {

	public static IJavaElement[] getSourcePackages(IJavaProject project, boolean includeArchive) {
		Set<IJavaElement> packages = new HashSet<IJavaElement>();
		Set<String> packageNames = new HashSet<String>();

		if (project != null && project.exists()) {
			for (IPackageFragmentRoot aSourceRoot : getSourceRoots(project)) {
				if (!includeArchive && aSourceRoot.isArchive()) continue;
				try {
					for (IJavaElement aPackage : aSourceRoot.getChildren()) {
						String packageName = aPackage.getElementName();
						if (!isDefaultPackage(packageName)
								&& !packageNames.contains(packageName)) {
							packageNames.add(packageName);
							packages.add(aPackage);
						}
					}
				} catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return packages.toArray(new IJavaElement[packages.size()]);
	}
	
	public static IJavaElement[] getDefaultSourcePackages(IJavaProject project, boolean includeArchive) {
		Set<IJavaElement> packages = new HashSet<IJavaElement>();

		if (project != null && project.exists()) {
			for (IPackageFragmentRoot aSourceRoot : getSourceRoots(project)) {
				if (!includeArchive && aSourceRoot.isArchive()) continue;
				try {
					for (IJavaElement aPackage : aSourceRoot.getChildren()) {
						if (isDefaultPackage(aPackage.getElementName())) {
							packages.add(aPackage);
						}
					}
				} catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return packages.toArray(new IJavaElement[packages.size()]);
	}

	public static boolean isDefaultPackage(String packageName) {
		return "".equals(packageName); //$NON-NLS-1$
	}

	public static IPackageFragmentRoot[] getSourceRoots(IJavaProject project) {
		ArrayList<IPackageFragmentRoot> result = new ArrayList<IPackageFragmentRoot>();
		try {
			IPackageFragmentRoot[] roots = project.getPackageFragmentRoots();
			for (int i = 0; i < roots.length; i++) {
				if (roots[i].getKind() == IPackageFragmentRoot.K_SOURCE) {
					result.add(roots[i]);
				}
			}
		} catch (JavaModelException ex) {

		}

		return result.toArray(new IPackageFragmentRoot[result.size()]);
	}
}
