import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import hudson.tasks.test.AbstractTestResultAction;
import hudson.model.Actionable;

def call(String buildStatus = 'Deployment Started', String channel = '#deployments') {

  // buildStatus of null means successfull
  buildStatus = buildStatus ?: 'UNSTABLE'
  channel = channel ?: '#deployments'


  // Default values
  def colorName = 'RED'
  def colorCode = '#FF0000'
  def subject = currentBuild.fullDisplayName.toString()
  def title = "${env.JOB_NAME} Build: ${env.BUILD_NUMBER}"
  def title_link = "${env.RUN_DISPLAY_URL}"

  // Override default values based on build status
  if (buildStatus == 'Deployment Started') {
    color = 'YELLOW'
    colorCode = '#FFFF00'
  } else if (buildStatus == 'Deployed to Test') {
    color = 'GREEN'
    colorCode = 'good'
  } else if (buildStatus == 'Deployed to Prod') {
    color = 'GREEN'
    colorCode = 'good'
  } else if (buildStatus == 'Build Comple') {
    color = 'GREEN'
    colorCode = 'good'
  } else if (buildStatus == 'UNSTABLE') {
    color = 'YELLOW'
    colorCode = 'warning'
  } else if (buildStatus == 'Approval Needed') {
	  color = 'YELLOW'
	  colorCode = '#FFFF00'
  } else {
    color = 'RED'
    colorCode = 'danger'
  }

  JSONObject attachment = new JSONObject();
 	attachment.put( 'author',      'Jenkins' )
	attachment.put( 'author_link', 'https://build.curtisgriffiths.co.uk' )
	attachment.put( 'title_link',  env.RUN_DISPLAY_URL )
	attachment.put( 'title',       'Status' )
  attachment.put( 'text', buildStatus )
	attachment.put('color',colorCode)

  JSONArray attachments = new JSONArray();
  attachments.add(attachment);
  println attachments.toString()

  // Send notifications
  slackSend (color: colorCode, message: subject, attachments: attachments.toString(), channel: channel)

}