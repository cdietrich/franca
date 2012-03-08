/*******************************************************************************
* Copyright (c) 2012 Harman International (http://www.harman.com).
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/
package org.franca.generators;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.franca.core.franca.FInterface;
import org.franca.core.franca.FModel;
import org.franca.generators.html.HTMLGenerator;
import org.franca.generators.java.JavaAPIGenerator;

public class FrancaGenerators {

	private FrancaGenerators() {
	}

	public boolean genHTML (FModel model, String outDir) {
		HTMLGenerator genHTML = new HTMLGenerator();
		String html = genHTML.generate(model).toString();
		return save(outDir, model.getName() + ".html", html);
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
	

	// singleton
	private static FrancaGenerators instance = null;
	public static FrancaGenerators instance() {
		if (instance==null) {
			instance = new FrancaGenerators();
		}
		return instance;
	}

	
	/**
	 * Helper for saving a text to file
	 *
	 * @param targetDir   the target directory
	 * @param filename    the target filename
	 * @param textToSave  the content for the file
	 * @return true if ok
	 */
	public static boolean save (String targetDir, String filename, String textToSave) {
		// ensure that directory is available
		File dir = new File(targetDir);
		if (! (dir.exists() || dir.mkdirs())) {
			System.err.println("Error: couldn't create directory " + targetDir + "!");
			return false;
		}
		
		// delete file prior to saving
		File file = new File(targetDir + "/" + filename);
		file.delete();
		
		// save contents to file
	    try {
	        BufferedWriter out = new BufferedWriter(new FileWriter(file));
	        out.write(textToSave);
	        out.close();
	        System.out.println("Created file " + file.getAbsolutePath());
	    } catch (IOException e) {
	    	return false;
	    }
	    
	    return true;
	}
	
}