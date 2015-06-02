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


/**
 * class containing information about targets
 * @author msk
 *
 */
public class TargetModel {

	private String uniprotId = "";
	private String name = "";
	private String geneName = "";
	private String drugbankId = "";
	private String organism = "";

	public String getUniprotId() {
		return uniprotId;
	}

	public void setUniprotId(String uniprotId) {
		this.uniprotId = uniprotId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGeneName() {
		return geneName;
	}

	public void setGeneName(String geneName) {
		this.geneName = geneName;
	}

	public String getDrugbankId() {
		return drugbankId;
	}

	public void setDrugbankId(String drugbankId) {
		this.drugbankId = drugbankId;
	}

	public String getOrganism() {
		return organism;
	}

	public void setOrganism(String organism) {
		this.organism = organism;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("[target:").append(" id=")
				.append(uniprotId).append(", name=").append(name)
				.append(", organism=").append(organism)
				.append(", geneName=").append(geneName).append("]");
		return sb.toString();
	}
}
