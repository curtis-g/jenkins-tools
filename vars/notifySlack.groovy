import net.sf.json.JSONObject
import net.sf.json.JSONArray

def prop( key, value )
{
	JSONObject prop = new JSONObject()

	prop.put( 'title', key )
	prop.put( 'value', value )
	prop.put( 'short', true )

	return prop
}

def call( params = null, param2 = null )
{
	if ( params == null )
	{
		params = [ 'message' : null ]
	}
	else if ( ! ( params instanceof Map ) )
	{
		if ( param2 instanceof Map )
		{
			param2.message = params
			params = param2
		}
		else if ( params.length() < 20 )
		{
			params = [ 'stage': params ]
		}
		else
		{
			params = [ 'message': params ]
		}
	}

	params.stage   = params.stage ?: env.STAGE_NAME
	params.channel = params.channel ?: 'deployments'

	if ( ! params.color )
	{
		switch ( currentBuild.currentResult.toString() )
		{
			case 'SUCCESS':
				params.color = 'good'
				break
			case 'UNSTABLE':
				params.color = 'warning'
				break
			case 'FAILURE':
				params.color = 'error'
				break
			default:
				params.color = '#000000'
		}
	}

	if ( ! params.status )
	{
		switch ( currentBuild.currentResult.toString() )
		{
			case 'SUCCESS':
				params.status = 'Good'
				break
			case 'UNSTABLE':
				params.status = 'Unstable'
				break
			case 'FAILURE':
				params.status = 'Failure'
				break
			default:
				params.status = 'Unknown'
		}
	}

	JSONObject attachment = new JSONObject()
	JSONArray  props      = new JSONArray()

	attachment.put( 'author',      'Jenkins' )
	attachment.put( 'author_link', 'https://build.curtisgriffiths.co.uk' )
	attachment.put( 'title_link',  env.RUN_DISPLAY_URL )
	attachment.put( 'title',       currentBuild.fullDisplayName.toString() )

	attachment.put( 'color',  params.color )

	if ( binding.hasVariable('scm') )
	{
		props.add( prop( 'Revision', scm.toString() ) )
	}
	else if ( binding.hasVariable('git') )
	{
		rev = git.GIT_COMMIT.substring(0, 6)
		props.add( prop( 'Revision', git.GIT_LOCAL_BRANCH + '@' + rev ) )
	}

	if ( env.TAG_NAME )
	{
		props.add( prop( 'Tag', env.TAG_NAME ) )
	}
	else if ( env.BRANCH_NAME )
	{
		props.add( prop( 'Branch', env.BRANCH_NAME ) )
	}

	props.add( prop( 'Stage' , params.stage.toString() ) )
	props.add( prop( 'Status', params.status ) )

	attachment.put( 'fields', props )

	JSONArray attachments = new JSONArray()
	attachments.add( attachment )

	// Send notifications
	slackSend(
		color: params.color, message: params.message,
		attachments: attachments.toString(), channel: params.channel
	)
}