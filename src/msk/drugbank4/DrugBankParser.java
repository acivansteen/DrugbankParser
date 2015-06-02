// Copyright 2015 mkutmon
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

package msk.drugbank4;


import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

/**
 * Download XML file from Drugbank (http://www.drugbank.ca/downloads)
 * Version 4
 * for version 4.1 at least 2g memory required
 * 
 * @author msk
 *
 */
public class DrugBankParser {

	private Namespace nsDrugBank;
	
	public DrugBankParser() {
		nsDrugBank = Namespace.getNamespace("http://www.drugbank.ca");
	}
	
	/**
	 * return a set of drugs
	 * DrugModel contains also the list of targets
	 */
	public Set<DrugModel> parse(File drugBankXml) throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();

		Document document = (Document) builder.build(drugBankXml);
		Element rootNode = document.getRootElement();
		
		Set<DrugModel> drugs = new HashSet<DrugModel>();
		
		List<Element> list = rootNode.getChildren("drug", nsDrugBank);
		for(Element drug : list) {
			DrugModel model = new DrugModel();
			
			Element id = drug.getChild("drugbank-id", nsDrugBank);
			model.setDrugbankID(id.getValue());
			
			Element name = drug.getChild("name", nsDrugBank);
			model.setName(name.getValue());
			
			Element cas = drug.getChild("cas-number", nsDrugBank);
			model.setCasNumber(cas.getValue());

			
			model.setInChiKey(getInchiKey(drug));
			model.getGroups().addAll(getGroups(drug));
			model.getCategories().addAll(getCategories(drug));
			model.getTargets().addAll(getTargets(drug));
			
			drugs.add(model);
		}
		
		return drugs;
	}
	
	/**
	 * reads targets for current drug
	 */
	private Set<TargetModel> getTargets(Element drug) {
		Set<TargetModel> set = new HashSet<TargetModel>();
		Element targets = drug.getChild("targets", nsDrugBank);
		if(targets != null) {
			List<Element> targetList = targets.getChildren("target", nsDrugBank);
			for(Element target : targetList) {
				TargetModel targetModel = new TargetModel();
				
				Element targetId = target.getChild("id", nsDrugBank);
				targetModel.setDrugbankId(targetId.getValue());
				
				Element targetName = target.getChild("name", nsDrugBank);
				targetModel.setName(targetName.getValue());
				
				Element targetOrganism = target.getChild("organism", nsDrugBank);
				targetModel.setOrganism(targetOrganism.getValue());
				
				Element polypeptide = target.getChild("polypeptide", nsDrugBank);
				if(polypeptide != null) {
					Element polypeptideGeneName = polypeptide.getChild("gene-name", nsDrugBank);	
					targetModel.setGeneName(polypeptideGeneName.getValue());
					
					Element xrefs = polypeptide.getChild("external-identifiers", nsDrugBank);
					List<Element> xrefList = xrefs.getChildren("external-identifier", nsDrugBank);
					for(Element xref : xrefList) {
						Element res = xref.getChild("resource", nsDrugBank);
						if(res.getValue().equals("UniProtKB")) {
							Element uniprot = xref.getChild("identifier", nsDrugBank);
							targetModel.setUniprotId(uniprot.getValue());
						}
					}
				}
				set.add(targetModel);
			}
		}
		return set;
	}
	
	/**
	 * reads categories for current drug
	 */
	private Set<String> getCategories(Element drug) {
		Set<String> set = new HashSet<String>();
		Element categories = drug.getChild("categories", nsDrugBank);
		if(categories != null) {
			List<Element> categoryList = categories.getChildren("category", nsDrugBank);
			for(Element category : categoryList) {
				Element cat = category.getChild("category", nsDrugBank);
				set.add(cat.getValue());
			}
		}
		return set;
	}
	
	/**
	 * reads groups for current drug
	 */
	private Set<String> getGroups(Element drug) {
		Set<String> set = new HashSet<String>();
		Element groups = drug.getChild("groups", nsDrugBank);
		if(groups != null) {
			List<Element> groupList = groups.getChildren("group", nsDrugBank);
			for(Element group : groupList) {
				set.add(group.getValue());
			}
		}
		return set;
	}
	
	/**
	 * reads inchikey for current drug
	 */
	private String getInchiKey(Element drug) {
		Element props = drug.getChild("calculated-properties", nsDrugBank);
		if(props != null) {
			List<Element> typeList = props.getChildren("property", nsDrugBank);
			for(Element type : typeList) {
				Element kind = type.getChild("kind", nsDrugBank);
				if(kind.getValue().equals("InChIKey")) {
					Element value = type.getChild("value", nsDrugBank);
					return value.getValue().substring(9);
					
				}
			}
		}
		return "";
	}
}
