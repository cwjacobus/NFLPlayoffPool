package actions;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;

import data.Pool;
import data.User;

public class SavePicksAction extends ActionSupport implements SessionAware {
	
	private static final long serialVersionUID = 1L;
	private Integer afcwc1;
	private Integer afcwc2;
	private Integer nfcwc1;
	private Integer nfcwc2;
	private Integer afcdiv1;
	private Integer afcdiv2;
	private Integer nfcdiv1;
	private Integer nfcdiv2;
	private Integer afcchamp;
	private Integer nfcchamp;
	private String sb;
	
	Map<String, Object> userSession;
	
	Map<Integer, String> nfcTeams = new HashMap<Integer, String>()
	{{
	     put(1, "NO");
	     put(2, "LAR");
	     put(3, "CHI");
	     put(4, "DAL");
	     put(5, "SEA");
	     put(6, "PHI");
	}};
	Map<Integer, String> afcTeams = new HashMap<Integer, String>()
	{{
	     put(1, "KC");
	     put(2, "NE");
	     put(3, "HOU");
	     put(4, "BAL");
	     put(5, "LAC");
	     put(6, "IND");
	}};

	public String execute() throws Exception {
		ValueStack stack = ActionContext.getContext().getValueStack();
	    Map<String, Object> context = new HashMap<String, Object>();
		if (userSession == null || userSession.size() == 0) {
			context.put("errorMsg", "Session has expired!");
			stack.push(context);
			return "error";
		}
		System.out.println("AFC WC: " + afcTeams.get(afcwc1) + " " + afcTeams.get(afcwc2));
		System.out.println("NFC WC: " + nfcTeams.get(nfcwc1) + " " + nfcTeams.get(nfcwc2));
		System.out.println("AFC DIV: " + afcTeams.get(afcdiv1) + " " + afcTeams.get(afcdiv2));
		System.out.println("NFC DIV: " + nfcTeams.get(nfcdiv1) + " " + nfcTeams.get(nfcdiv2));
		System.out.println("CHAMPS: " + afcTeams.get(afcchamp) + " " + nfcTeams.get(nfcchamp));
		System.out.println("SB: " + sb);
		
		Integer year = (Integer)userSession.get("year");
		User user = (User)userSession.get("user");
		Pool pool = (Pool)userSession.get("pool");
		
	    stack.push(context);
	    return "success";
	}

	public Integer getAfcwc1() {
		return afcwc1;
	}

	public void setAfcwc1(Integer afcwc1) {
		this.afcwc1 = afcwc1;
	}

	public Integer getAfcwc2() {
		return afcwc2;
	}

	public void setAfcwc2(Integer afcwc2) {
		this.afcwc2 = afcwc2;
	}

	public Integer getNfcwc1() {
		return nfcwc1;
	}

	public void setNfcwc1(Integer nfcwc1) {
		this.nfcwc1 = nfcwc1;
	}

	public Integer getNfcwc2() {
		return nfcwc2;
	}

	public void setNfcwc2(Integer nfcwc2) {
		this.nfcwc2 = nfcwc2;
	}

	public Integer getAfcdiv1() {
		return afcdiv1;
	}

	public void setAfcdiv1(Integer afcdiv1) {
		this.afcdiv1 = afcdiv1;
	}

	public Integer getAfcdiv2() {
		return afcdiv2;
	}

	public void setAfcdiv2(Integer afcdiv2) {
		this.afcdiv2 = afcdiv2;
	}

	public Integer getNfcdiv1() {
		return nfcdiv1;
	}

	public void setNfcdiv1(Integer nfcdiv1) {
		this.nfcdiv1 = nfcdiv1;
	}

	public Integer getNfcdiv2() {
		return nfcdiv2;
	}

	public void setNfcdiv2(Integer nfcdiv2) {
		this.nfcdiv2 = nfcdiv2;
	}

	public Integer getAfcchamp() {
		return afcchamp;
	}

	public void setAfcchamp(Integer afcchamp) {
		this.afcchamp = afcchamp;
	}

	public Integer getNfcchamp() {
		return nfcchamp;
	}

	public void setNfcchamp(Integer nfcchamp) {
		this.nfcchamp = nfcchamp;
	}

	public String getSb() {
		return sb;
	}

	public void setSb(String sb) {
		this.sb = sb;
	}

	@Override
    public void setSession(Map<String, Object> sessionMap) {
        this.userSession = sessionMap;
    }

}
