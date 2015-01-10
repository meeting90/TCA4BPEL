package cn.edu.nju.cs.ctao4bpel.alang;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ode.bpel.compiler.bom.Activity;
import org.apache.ode.bpel.compiler.bom.Catch;
import org.apache.ode.bpel.compiler.bom.CompensationHandler;
import org.apache.ode.bpel.compiler.bom.CompositeActivity;
import org.apache.ode.bpel.compiler.bom.FaultHandler;
import org.apache.ode.bpel.compiler.bom.IfActivity;
import org.apache.ode.bpel.compiler.bom.IfActivity.Case;
import org.apache.ode.bpel.compiler.bom.OnAlarm;
import org.apache.ode.bpel.compiler.bom.OnEvent;
import org.apache.ode.bpel.compiler.bom.OnMessage;
import org.apache.ode.bpel.compiler.bom.PickActivity;
import org.apache.ode.bpel.compiler.bom.Process;
import org.apache.ode.bpel.compiler.bom.RepeatUntilActivity;
import org.apache.ode.bpel.compiler.bom.Scope;
import org.apache.ode.bpel.compiler.bom.ScopeActivity;
import org.apache.ode.bpel.compiler.bom.ScopeLikeActivity;
import org.apache.ode.bpel.compiler.bom.SwitchActivity;
import org.apache.ode.bpel.compiler.bom.TerminationHandler;
import org.apache.ode.bpel.compiler.bom.WhileActivity;
import org.apache.ode.utils.msg.MessageBundle;
import org.w3c.dom.Element;


/**
 * 
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-7 2015
 * ActivityFunctionImpl.java
 */
public class ActivityFunctionImpl implements ActivityFunction{
	
	private static final Log log = LogFactory.getLog(ActivityFunctionImpl.class);
	private static final Pattern functionPattern=Pattern.compile("\\s*activity\\s*\\((.*)\\)\\s*");
	private static final InterpreterMessage _imsgs= MessageBundle.getMessages(InterpreterMessage.class);
	private static final String commaSymbol = ",";
	private static final String equalSymbol="=";
	
	enum MatchResult{
		NotSpecified,
		NotMatched,
		Matched
	}
	@Override
	public ActivityFunctionStruct interpreter(String expression)
			throws InterpreterException {
		try{
			Matcher matcher = functionPattern.matcher(expression);
			if (!matcher.matches())
				throw new  InterpreterException(_imsgs.msgSyntaxErr());
			else{
				String content = matcher.group(1);
				log.debug(content);
				if(content.matches("\\*")){//activity(*)
					return new ActivityFunctionStruct(null,null,null,null,null);
				}else{
					String[] equations = content.split(commaSymbol);
					ActivityFunctionStruct.StructElement  xpath =null, type =null, name= null,partnerlink=null, operation = null;
					for(String equation: equations){
						String []pair = equation.split(equalSymbol);
						if(pair.length != 2)
							throw new InterpreterException(_imsgs.msgSyntaxErr());
						String key = pair[0].trim().toLowerCase();
						String value = pair[1].replace("\"", "").trim();
						if(key.equals(ActivityFunctionStruct.XPATH_KEY)){
							xpath = new ActivityFunctionStruct.Xpath(value);
						}else if(key.equals(ActivityFunctionStruct.NAME_KEY)){
							name = new ActivityFunctionStruct.NamePattern(value);
						}else if(key.equals(ActivityFunctionStruct.TYPE_KEY)){
							type= new ActivityFunctionStruct.TypePattern(value);
						}else if(key.equals(ActivityFunctionStruct.OPERATION_KEY)){
							operation = new ActivityFunctionStruct.OperationPattern(value);
						}else if(key.equals(ActivityFunctionStruct.PARTNERLINK_KEY)){
							partnerlink= new ActivityFunctionStruct.PartnerlinkPattern(value);
						}else{
							throw new InterpreterException(_imsgs.msgParamErr());
						}
					}
					ActivityFunctionStruct struct = new ActivityFunctionStruct(
							(ActivityFunctionStruct.Xpath)xpath, 
							(ActivityFunctionStruct.NamePattern)name, 
							(ActivityFunctionStruct.TypePattern)type, 
							(ActivityFunctionStruct.OperationPattern)operation, 
							(ActivityFunctionStruct.PartnerlinkPattern)partnerlink);
					struct.syntaxValidate();
					return struct;
					
				}
			}
		}catch(Exception e){
			throw new InterpreterException(e);
		}
	
	}

	@Override
	public Collection<Activity> getActivities(ActivityFunctionStruct struct, 
			Process process) {
		try{
			if(struct.getXpath()!=null){
				return getActivitiesWithXpath(struct, process);
			}else{
				return getActivitiesWithOutXpath(struct, process);
			}
		}catch(Exception e){
			throw new InterpreterException(e);
		}
	}
	
	private Collection<Activity> getActivitiesWithXpath(ActivityFunctionStruct struct, 
			Process process) throws XPathExpressionException{
		
		assert(struct.getXpath() != null);
		Element element = struct.getXpath().getElement(process);
		if(element == null)
			throw new InterpreterException(_imsgs.msgMatchFailed(struct.getXpath(), element));
		List<ActivityFunctionStruct.StructElement> ses = struct.getStructElement();
		for(ActivityFunctionStruct.StructElement se: ses){
			if(se instanceof ActivityFunctionStruct.Xpath)
				continue;
			MatchResult matchResult = matchRegular(element, se);
			switch (matchResult) {
			case NotSpecified:
				throw new InterpreterException(_imsgs.msgAttrNotSpecified(se.key, element));
			case NotMatched:
				throw new InterpreterException(_imsgs.msgMatchFailed(se, element));
			case Matched:
				break;
			}
		}
		
	
		Activity result = element2Activity(element);
		
		Collection<Activity> activities = Collections.singletonList(result);
		return activities;
	}
	
	
	
	private Collection<Activity> getActivitiesWithOutXpath(ActivityFunctionStruct struct, Process process){
		Collection<Activity> result = Collections.emptyList();
		Collection<Activity> candidate=Collections.emptyList();
		Activity root = process.getRootActivity();
		candidate.add(root);
		List<OnAlarm> alarms = process.getAlarms();
		for(OnAlarm alarm: alarms)
			candidate.add(alarm.getActivity());
		List<OnEvent> events =process.getEvents();
		for(OnEvent event: events)
			candidate.add(event.getActivity());
		FaultHandler fh = process.getFaultHandler();
		if(fh!=null)
			for(Catch _catch: fh.getCatches())
				candidate.add(_catch.getActivity());
		CompensationHandler ch=process.getCompensationHandler();
		if(ch!=null)
			candidate.add(ch.getActivity());
		TerminationHandler th=process.getTerminationHandler();
		if(th!=null)
			candidate.add(th.getActivity());
		
		for(Activity activity : candidate){
			Collection<Activity> allChildren= bfsChidrenActivities(activity);
			for(Activity act: allChildren){
				boolean isMatched = checkIfRegularMatchForActivities(act, struct);
				if(isMatched)
					result.add(act);
			}
		}
		return result;
	}
	
	/**
	 * BFS scan all the children of an activity
	 * @param result
	 * @param bob
	 * @param struct
	 */

	private Collection<Activity> bfsChidrenActivities(Activity activity){
		List<Activity> activities = Collections.emptyList();
		activities.add(activity);
		int index =0;
		while(index < activities.size() ){
			activity = activities.get(index);
			if(activity instanceof CompositeActivity){
				CompositeActivity ca=(CompositeActivity)activity;
				activities.addAll(ca.getActivities());
			}else if(activity instanceof ScopeLikeActivity){
				ScopeLikeActivity sla= (ScopeLikeActivity)activity;
				Scope scope =sla.getScope();
				List<OnAlarm> alarms = scope.getAlarms();
				for(OnAlarm alarm: alarms)
					activities.add(alarm.getActivity());
				List<OnEvent> events =scope.getEvents();
				for(OnEvent event: events)
					activities.add(event.getActivity());
				FaultHandler fh = scope.getFaultHandler();
				if(fh!=null)
					for(Catch _catch: fh.getCatches())
						activities.add(_catch.getActivity());
				CompensationHandler ch=scope.getCompensationHandler();
				if(ch!=null)
					activities.add(ch.getActivity());
				TerminationHandler th=scope.getTerminationHandler();
				if(th!=null)
					activities.add(th.getActivity());
				if(sla instanceof ScopeActivity){
					ScopeActivity sa= (ScopeActivity)sla;
					activities.add(sa.getChildActivity());
				}
			}
			else if(activity instanceof PickActivity){
				PickActivity pa=(PickActivity) activity;
				for (OnAlarm alarm :pa.getOnAlarms())
					activities.add(alarm.getActivity());
				for(OnMessage msg: pa.getOnMessages()){
					activities.add(msg.getActivity());
				}
			}else if(activity instanceof IfActivity){
				IfActivity ifa= (IfActivity)activity;
				activities.add(ifa.getActivity());
				for(Case _case:ifa.getCases() )
					activities.add(_case.getActivity());
			}else if(activity instanceof SwitchActivity){
				SwitchActivity sa= (SwitchActivity) activity;
				for(org.apache.ode.bpel.compiler.bom.SwitchActivity.Case _case:sa.getCases() )
					activities.add(_case.getActivity());
			}
			else if(activity instanceof RepeatUntilActivity){
				RepeatUntilActivity rua= (RepeatUntilActivity) activity;
				activities.add(rua.getActivity());
			}else if(activity instanceof WhileActivity){
				WhileActivity wa= (WhileActivity)activity;
				activities.add(wa.getActivity());
			}
			index ++;
			
		}
		return activities;
	}
	
	
	private boolean checkIfRegularMatchForActivities(Activity activity,ActivityFunctionStruct struct ){
		List<ActivityFunctionStruct.StructElement> ses = struct.getStructElement();
		for(ActivityFunctionStruct.StructElement se: ses){
			if(se instanceof ActivityFunctionStruct.Xpath)
				continue;
			MatchResult matchResult = matchRegular(activity.getElement(), se);
			if(matchResult!= MatchResult.Matched)
				return false;
		}
		return true;
	}
	/**
	 * @param element
	 * @return
	 */
	private Activity element2Activity(Element element) {
		return null;
	}

	
	
	private MatchResult matchRegular(Element element, ActivityFunctionStruct.StructElement regular){
		
		String matcher =null;
		if(ActivityFunctionStruct.TYPE_KEY.equals(regular.key))
			matcher =element.getNodeName();
		else 
			matcher =element.getAttribute(regular.key);
		if(matcher==null)
			return MatchResult.NotSpecified;
		if(!matcher.matches(regular.expression))
			return MatchResult.NotMatched;
		return MatchResult.Matched;
	}
	

	
	

}
