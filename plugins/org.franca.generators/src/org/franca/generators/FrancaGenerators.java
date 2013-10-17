/*******************************************************************************
* Copyright (c) 2012 Harman International (http://www.harman.com).
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/
package org.franca.generators;

import org.eclipse.emf.ecore.EObject;
import org.franca.core.franca.FInterface;
import org.franca.core.franca.FModel;
import org.franca.core.utils.FileHelper;
import org.franca.generators.html.HTMLGenerator;
import org.franca.generators.java.JavaAPIGenerator;
import org.franca.generators.websocket.WebsocketProxyGenerator;

public class FrancaGenerators {

	private FrancaGenerators() { }

	public boolean genHTML (FModel model, String outDir) {
		HTMLGenerator genHTML = new HTMLGenerator();
		String html = genHTML.generate(model).toString();
		String basename = getBasename(model);
		String outPath = outDir + "/" + createPath(model);
		return FileHelper.save(outPath, basename + ".html", html);
	}
	
	
	public boolean genJava (String srcGenDir, String pkg, FModel model) {
		String genDir = srcGenDir + "/" + pkg.replace(".", "/") + "/";

		// we pick the first interface only
		FInterface api = model.getInterfaces().get(0);

		JavaAPIGenerator genJavaAPI = new JavaAPIGenerator();
		if (! save(genDir,
				genJavaAPI.getInterfaceFilename(api),
				genJavaAPI.generateInterface(api, pkg).toString()
				)) {
			return false;
		}
		if (! save(genDir,
				genJavaAPI.getServerBaseFilename(api),
				genJavaAPI.generateServerBase(api, pkg).toString()
				)) {
			return false;
		}

		return true;
	}
	
	public boolean genWebsocketClient (FModel model, String outDir) {
		WebsocketProxyGenerator genClient = new WebsocketProxyGenerator();

		// we pick the first interface only
		FInterface api = model.getInterfaces().get(0);

		String output = genClient.generate(api).toString();
		String outPath = outDir + "/" + createPath(model);
		return FileHelper.save(outPath, genClient.getProxyName(api) + ".js", output);
	}
	

	// singleton
	private static FrancaGenerators instance = null;
	public static FrancaGenerators instance() {
		if (instance==null) {
			instance = new FrancaGenerators();
		}
		return instance;
	}

	/**
	 * Get the basename of a resource, given one EObject of this resource.
	 * 
	 * @param obj an EObject, whose resource's basename will be computed.
	 * 
	 * @return the basename of the EObject's resource
	 */
	public static String getBasename (EObject obj) {
		String filename = obj.eResource().getURI().lastSegment();
		String basename = filename.substring(0, filename.lastIndexOf('.'));
		return basename;
	}

	/**
	 * Transform a model's package FQN into a relative directory path.
	 */
	public static String createPath (FModel fmodel) {
		return fmodel.getName().replace(".", "/");
	}

	/**
	 * Helper for saving a text to file
	 *
	 * @deprecated use FileHelper.save() instead
	 */
	public static boolean save (String targetDir, String filename, String textToSave) {
		return FileHelper.save(targetDir, filename, textToSave);
	}
	
}
