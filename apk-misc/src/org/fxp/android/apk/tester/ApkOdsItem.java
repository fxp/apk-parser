package org.fxp.android.apk.tester;

public class ApkOdsItem {
	private String id;
	private String name;
	private String categoryID;
	private String developer;
	private String version;
	private String language;
	private String flag;
	private String apkLocation;
	private String snapshots;
	private String description;
	private String shortDescription;
	private String homepage;

	private String csvFormat;

	private void addField(String field) {
		csvFormat += field + ",";
	}

	public String toString() {
		csvFormat = "";
		addField(id);
		addField(name);
		addField(categoryID);
		addField(developer);
		addField(version);
		addField(language);
		addField(flag);
		addField(apkLocation);
		addField(snapshots);
		addField(description);
		addField(shortDescription);
		addField(homepage);

		return csvFormat;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(String categoryID) {
		this.categoryID = categoryID;
	}

	public String getDeveloper() {
		return developer;
	}

	public void setDeveloper(String developer) {
		this.developer = developer;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getApkLocation() {
		return apkLocation;
	}

	public void setApkLocation(String apkLocation) {
		this.apkLocation = apkLocation;
	}

	public String getSnapshots() {
		return snapshots;
	}

	public void setSnapshots(String snapshots) {
		this.snapshots = snapshots;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getHomepage() {
		return homepage;
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

}
