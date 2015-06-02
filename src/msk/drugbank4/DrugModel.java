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


import java.util.HashSet;
import java.util.Set;

/**
 * class containing information about drugs
 * @author msk
 *
 */
public class DrugModel {
	private String drugbankID = "";
	private String name = "";
	private String InChiKey = "";
	private String casNumber = "";
	private Set<String> groups;
	private Set<String> categories;
	private Set<TargetModel> targets;
	
	public DrugModel() {
		categories = new HashSet<String>();
		targets = new HashSet<TargetModel>();
		groups = new HashSet<String>();
	}
	
	

	public String getDrugbankID() {
		return drugbankID;
	}

	public void setDrugbankID(String drugbankID) {
		this.drugbankID = drugbankID;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getInChiKey() {
		return InChiKey;
	}

	public void setInChiKey(String inChiKey) {
		InChiKey = inChiKey;
	}

	public String getCasNumber() {
		return casNumber;
	}

	public void setCasNumber(String casNumber) {
		this.casNumber = casNumber;
	}

	public Set<String> getGroups() {
		return groups;
	}

	public Set<String> getCategories() {
		return categories;
	}

	public Set<TargetModel> getTargets() {
		return targets;
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(drugbankID.length()
				+ name.length() + InChiKey.length());

		sb.append("[drug:").append(" id=")
				.append(drugbankID).append(", name=").append(name)
				.append(", inchikey=").append(InChiKey)
				.append(", categories=").append(categories)
				.append(", targets=").append(targets)
				.append("]");
		return sb.toString();
	}
}
