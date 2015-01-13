package org.jenkinsci.plugins.changeassemblyversion;

public class AssemblyVersion {
	
	private Integer major;
    private Integer minor;
    private Integer build;
    private Integer revision;

	
	/**
	 * The instance of this class gonna return in the property version the value to be used on ChangeTools.
	 * @param version
	 */
	public AssemblyVersion(String version){
        assert version != null;

        String[] tokens = version.split("\\.");
        switch (tokens.length) {
            case 4:
                this.revision = Integer.parseInt(tokens[3]);
            case 3:
                this.build = Integer.parseInt(tokens[2]);
            case 2:
                this.minor = Integer.parseInt(tokens[1]);
            case 1:
                this.major = Integer.parseInt(tokens[0]);
        }
	}

    public void setMajor(Integer major) {
        this.major = major;
    }

    public void setMinor(Integer minor) {
        this.minor = minor;
    }

    public void setBuild(Integer build) {
        this.build = build;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public Integer getMajor() {
        return this.major;
    }

    public Integer getMinor() {
        return this.minor;
    }

    public Integer getBuild() {
        return this.build;
    }

    public Integer getRevision() {
        return this.revision;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.getMajor());
        Integer[] parts = { this.getMinor(), this.getBuild(), this.getRevision() };
        for(Integer part : parts) {
            if (part != null) {
                sb.append(".");
                sb.append(part);
            } else {
                break;
            }
        }
        return sb.toString();
    }

    public String toString(int length) {
        assert length >= 1;
        assert length <= 4;

        StringBuffer sb = new StringBuffer();
        sb.append(this.getMajor());
        int left = length - 1;
        Integer[] parts = { this.getMinor(), this.getBuild(), this.getRevision() };
        for(Integer part : parts) {
            if (left == 0) {
                break;
            }
            sb.append(".");
            if (part != null) {
                sb.append(part);
            } else {
                sb.append(0);
            }
            left--;
        }
        return sb.toString();

    }
}
