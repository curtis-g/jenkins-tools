import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import hudson.tasks.test.AbstractTestResultAction;
import hudson.model.Actionable;

def prop( key, value )
{
	JSONObject prop = new JSONObject()

	prop.put( 'title', key )
	prop.put( 'value', value )
	prop.put( 'short', true )

	return prop
}

def call(String buildStatus = 'Deployment Started', String channel = '#deployments') {

  // buildStatus of null means successfull
  buildStatus = buildStatus ?: 'UNSTABLE'
  channel = channel ?: '#deployments'


  // Default values
  def colorName = 'RED'
  def colorCode = '#FF0000'
  def subject = "Deployment of *${env.JOB_NAME.toString()}*"
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
  } else if (buildStatus == 'Build Complete') {
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
  JSONArray  props      = new JSONArray()

 	attachment.put( 'author',      'Jenkins' )
	attachment.put( 'author_link', 'https://build.curtisgriffiths.co.uk' )
	attachment.put( 'title_link',  env.RUN_DISPLAY_URL )
	attachment.put( 'title',       env.JOB_URL )
  attachment.put( 'text', env.GIT_COMMIT)
	attachment.put('color',colorCode)
  attachment.put('mrkdwn_in', ['subject'])
  

  props.add( prop('Status', buildStatus))
  props.add( prop('Build Number', env.BUILD_NUMBER))

  attachment.put( 'fields', props)

  JSONArray attachments = new JSONArray();
  attachments.add(attachment);
  println attachments.toString()

  // Send notifications
  slackSend (color: colorCode, message: subject, attachments: attachments.toString(), channel: channel)

}