package actions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;

import dao.DAO;
import data.Pool;
import data.User;

public class SavePicksAction extends ActionSupport implements SessionAware {
	
	private static final long serialVersionUID = 1L;
	private String afcwc1;
	private String afcwc2;
	private String nfcwc1;
	private String nfcwc2;
	private String afcdiv1;
	private String afcdiv2;
	private String nfcdiv1;
	private String nfcdiv2;
	private String afcchamp;
	private String nfcchamp;
	private String sb;
	
	Map<String, Object> userSession;
	Pool pool;
	User user;
	int firstGameIndex;
	
	String[] gameCodes = {"afcwc1", "afcwc2", "nfcwc1", "nfcwc2", "afcdiv1", "afcdiv2", "nfcdiv1", "nfcdiv2", "afcchamp", "nfcchamp", "sb"};

	public String execute() throws Exception {
		ValueStack stack = ActionContext.getContext().getValueStack();
	    Map<String, Object> context = new HashMap<String, Object>();
		if (userSession == null || userSession.size() == 0) {
			context.put("errorMsg", "Session has expired!");
			stack.push(context);
			return "error";
		}
		if (afcwc1 == null || afcwc1.equals("0") || afcwc2 == null || afcwc2.equals("0") || nfcwc1 == null || nfcwc1.equals("0") || 
			nfcwc2 == null || nfcwc2.equals("0") || afcdiv1 == null || afcdiv1.equals("0") || afcdiv2 == null || afcdiv2.equals("0") || 
			nfcdiv1 == null || nfcdiv1.equals("0") || nfcdiv2 == null || nfcdiv2.equals("0") || afcchamp == null || afcchamp.equals("0") ||
			nfcchamp == null || nfcchamp.equals("0") || sb == null || sb.equals("0")) {
				context.put("errorMsg", "All games must be picked!");
				stack.push(context);
				return "error";
		}
		user = (User)userSession.get("user");
		pool = (Pool)userSession.get("pool");
		firstGameIndex = DAO.getFirstGameIndexForAYear(pool.getYear());
		
		/*System.out.println("AFC WC: " + afcwc1 + " " + afcwc2);
		System.out.println("NFC WC: " + nfcwc1 + " " + nfcwc2);
		System.out.println("AFC DIV: " + afcdiv1 + " " + afcdiv2);
		System.out.println("NFC DIV: " + nfcdiv1 + " " + nfcdiv2);
		System.out.println("CHAMPS: " + afcchamp + " " + nfcchamp);
		System.out.println("SB: " + sb);*/
		
		DAO.deletePicksByUserIdAndPoolId(user.getUserId(), pool.getPoolId());
		Thread.sleep(1000);
		
		makePick(afcwc1);
		makePick(afcwc2);
		makePick(nfcwc1);
		makePick(nfcwc2);
		makePick(afcdiv1);
		makePick(afcdiv2);
		makePick(nfcdiv1);
		makePick(nfcdiv2);
		makePick(afcchamp);
		makePick(nfcchamp);
		makePick(sb);
	    stack.push(context);
	    return "success";
	}
	
	private void makePick(String game) {
		String[] valueArray = game.split(":");
		int arrayIndex = Arrays.asList(gameCodes).indexOf(valueArray[2]);
		DAO.createPick(user.getUserId(), (firstGameIndex + arrayIndex), valueArray[1], pool.getPoolId());
		//System.out.println("Create Pick: User: " + user.getUserName() + " Game index: " + (firstGameIndex + arrayIndex) + 
			//" Winner: " + valueArray[1] + " Pool: " + pool.getPoolId());
	}

	public String getAfcwc1() {
		return afcwc1;
	}

	public void setAfcwc1(String afcwc1) {
		this.afcwc1 = afcwc1;
	}

	public String getAfcwc2() {
		return afcwc2;
	}

	public void setAfcwc2(String afcwc2) {
		this.afcwc2 = afcwc2;
	}

	public String getNfcwc1() {
		return nfcwc1;
	}

	public void setNfcwc1(String nfcwc1) {
		this.nfcwc1 = nfcwc1;
	}

	public String getNfcwc2() {
		return nfcwc2;
	}

	public void setNfcwc2(String nfcwc2) {
		this.nfcwc2 = nfcwc2;
	}

	public String getAfcdiv1() {
		return afcdiv1;
	}

	public void setAfcdiv1(String afcdiv1) {
		this.afcdiv1 = afcdiv1;
	}

	public String getAfcdiv2() {
		return afcdiv2;
	}

	public void setAfcdiv2(String afcdiv2) {
		this.afcdiv2 = afcdiv2;
	}

	public String getNfcdiv1() {
		return nfcdiv1;
	}

	public void setNfcdiv1(String nfcdiv1) {
		this.nfcdiv1 = nfcdiv1;
	}

	public String getNfcdiv2() {
		return nfcdiv2;
	}

	public void setNfcdiv2(String nfcdiv2) {
		this.nfcdiv2 = nfcdiv2;
	}

	public String getAfcchamp() {
		return afcchamp;
	}

	public void setAfcchamp(String afcchamp) {
		this.afcchamp = afcchamp;
	}

	public String getNfcchamp() {
		return nfcchamp;
	}

	public void setNfcchamp(String nfcchamp) {
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
