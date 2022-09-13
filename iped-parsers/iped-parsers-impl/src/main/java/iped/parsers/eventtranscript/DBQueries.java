package iped.parsers.eventtranscript;

/**
 * SQLite queries used to parse the EventTranscript database
 * Mostly adapted from https://github.com/kacos2000/Queries#:~:text=Windows%2010/11%20diagnostics%20stuff
*/
public final class DBQueries {

    public static final String HISTORY = "SELECT"
        + " UserSID,"
        + " json_extract(JSONPayload,'$.data.CorrelationGuid') AS CorrelationGuid,"
        + " UTCTimestamp,"
        + " LocalTimestamp,"
        + " Timezone,"
        + " seq,"
        + " replace(group_concat(DISTINCT TagName), ',', ';') AS TagNames,"
        + " replace(group_concat(DISTINCT EventName), ',', ';') AS EventNames,"
        + " URL,"
        + " ReferURL,"
        + " App,"
        + " replace(group_concat(DISTINCT nullif(PageTitle, '')), ',', ';') AS PageTitles,"
        + " JSONPayload"
        + " FROM ("
        + " SELECT events_persisted.sid AS UserSID,"
        + "     replace(substr(json_extract(events_persisted.payload,'$.time'),1,19), 'T', ' ') as 'UTCTimestamp',"
        + "     datetime((timestamp - 116444736000000000)/10000000, 'unixepoch','localtime') as 'LocalTimestamp',"
        + "     json_extract(events_persisted.payload,'$.ext.loc.tz') as 'Timezone',"
        + "     json_extract(events_persisted.payload,'$.ext.utc.seq') AS 'seq',"
        + "     tag_descriptions.tag_name AS TagName,"
        + "     events_persisted.full_event_name AS FullEventName,"
        + "     replace(replace(substr(distinct events_persisted.full_event_name,39),'Microsoft.',''),'WebBrowser.HistoryJournal.','') AS 'EventName',"
        + "     json_extract(events_persisted.payload,'$.ext.app.name') AS App,"
        + "     events_persisted.compressed_payload_size AS CompressedPayloadSize,"
        + "     json_extract(events_persisted.payload,'$.data.navigationUrl') AS URL,"
        + "     json_extract(events_persisted.payload,'$.data.referUrl') AS ReferURL,"
        + "     json_extract(events_persisted.payload,'$.data.PageTitle') AS PageTitle,"
        + "     events_persisted.payload AS JSONPayload"
        + " FROM"
        + "     events_persisted"
        + "     LEFT JOIN event_tags ON events_persisted.full_event_name_hash = event_tags.full_event_name_hash"
        + "     LEFT JOIN tag_descriptions ON event_tags.tag_id = tag_descriptions.tag_id"
        + "     INNER JOIN provider_groups ON events_persisted.provider_group_id = provider_groups.group_id"
        + " WHERE"
        + "     (tag_descriptions.tag_name='Browsing History' AND events_persisted.full_event_name LIKE '%HistoryAddUrl') OR"
        + "     (tag_descriptions.tag_name='Product and Service Usage' AND events_persisted.full_event_name LIKE '%HistoryAddUrlEx')"
        + " )"
        + " GROUP BY CorrelationGuid"
        + " ORDER BY UTCTimestamp DESC";

        public static final String INVENTORY_APPS = "SELECT"
        + " replace(substr(json_extract(events_persisted.payload,'$.time'),1,19), 'T', ' ') as 'UTCTimeStamp',"
        + " datetime((timestamp - 116444736000000000)/10000000, 'unixepoch','localtime') AS 'LocalTimestamp',"
        + " json_extract(events_persisted.payload,'$.ext.loc.tz') AS 'Timezone',"
        + " json_extract(events_persisted.payload,'$.ext.utc.seq') AS 'seq',"
        + " tag_descriptions.tag_name AS TagName,"
        + " replace(events_persisted.full_event_name,'Microsoft.Windows.Inventory.Core.Inventory','') AS 'EventName',"
        + " events_persisted.full_event_name AS 'FullEventName',"
        + " json_extract(events_persisted.payload,'$.data.Type') AS 'Type',"
        + " json_extract(events_persisted.payload,'$.data.Name') AS 'Name',"
        + " json_extract(events_persisted.payload,'$.data.PackageFullName') AS 'PackageFullName',"
        + " json_extract(events_persisted.payload,'$.data.Version') AS 'Version',"
        + " json_extract(events_persisted.payload,'$.data.Publisher') AS 'Publisher',"
        + " json_extract(events_persisted.payload,'$.data.RootDirPath') AS 'RootDirPath',"
        + " json_extract(events_persisted.payload,'$.data.HiddenArp') AS 'HiddenArp',"
        + " json_extract(events_persisted.payload,'$.data.InstallDate') AS 'InstallDate',"
        + " json_extract(events_persisted.payload,'$.data.Source') AS 'Source',"
        + " json_extract(events_persisted.payload,'$.data.OSVersionAtInstallTime') AS 'OSVersionAtInstallTime',"
        + " json_extract(events_persisted.payload,'$.data.InstallDateMsi') AS 'MsiInstallDate',"
        + " json_extract(events_persisted.payload,'$.data.MsiPackageCode') AS 'MsiPackageCode',"
        + " json_extract(events_persisted.payload,'$.data.MsiProductCode') AS 'MsiProductCode',"
        + " json_extract(events_persisted.payload,'$.data.baseData.objectInstanceId') AS 'InstanceId',"
        + " trim(json_extract(events_persisted.payload,'$.ext.user.localId'),'m:') AS 'UserId',"
        + " sid AS 'UserSID',"
        + " events_persisted.payload AS JSONPayload"
        + " FROM"
        + "     events_persisted"
        + "     LEFT JOIN event_tags ON events_persisted.full_event_name_hash = event_tags.full_event_name_hash"
        + "     LEFT JOIN tag_descriptions ON event_tags.tag_id = tag_descriptions.tag_id"
        + " WHERE"
        + " FullEventName LIKE 'Microsoft.Windows.Inventory.Core.Inventory%' AND"
        + " TagName = 'Software Setup and Inventory'"
        + " ORDER BY cast(Timestamp AS integer) DESC";

    public static final String APP_INTERACTIVITY = "SELECT"
        + " replace(substr(json_extract(events_persisted.payload,'$.time'),1,19), 'T', ' ') as 'UTCTimeStamp',"
        + " datetime((timestamp - 116444736000000000)/10000000, 'unixepoch','localtime') AS 'LocalTimestamp',"
        + " json_extract(events_persisted.payload,'$.ext.loc.tz') AS 'Timezone',"
        + " json_extract(events_persisted.payload,'$.ext.utc.seq') as 'seq',"
        + " tag_descriptions.tag_name as TagName,"
        + " replace(events_persisted.full_event_name,'Win32kTraceLogging.','') AS 'EventName',"
        + " replace(substr(json_extract(events_persisted.payload,'$.data.AggregationStartTime'),1,19), 'T', ' ') AS 'AggregationStartTime', "
        + " json_extract(events_persisted.payload,'$.data.AggregationDurationMS') AS 'AggregationDurationMS', "
        + " CASE WHEN substr(json_extract(events_persisted.payload,'$.data.AppId'),1,1) is 'W' "
        + "      THEN substr(json_extract(events_persisted.payload,'$.data.AppId'),93)"
        + "      WHEN substr(json_extract(events_persisted.payload,'$.data.AppId'),1,1) is 'U' "
        + "      THEN substr(json_extract(events_persisted.payload,'$.data.AppId'),3)"
        + "      ELSE json_extract(events_persisted.payload,'$.data.AppId') "
        + "      END AS 'AppId',"
        + " CASE WHEN substr(json_extract(events_persisted.payload,'$.data.AppId'),1,1) is 'W' "
        + "     THEN substr(json_extract(events_persisted.payload,'$.data.AppVersion'),21,(instr(substr(json_extract(events_persisted.payload,'$.data.AppVersion'),21),'!')-1) )"
        + "     END AS 'PE Header CheckSum',	"
        + " CASE substr(json_extract(events_persisted.payload,'$.data.AppId'),1,1) "
        + "     WHEN 'W' THEN 'Win' "
        + "     WHEN 'U' THEN 'UWP' "
        + "     END AS 'Type',	"
        + " json_extract(events_persisted.payload,'$.data.WindowWidth')||'x'||json_extract(events_persisted.payload,'$.data.WindowHeight') AS 'WindowSize(WxH)',	"
        + " json_extract(events_persisted.payload,'$.data.MouseInputSec') AS 'MouseInputSec', "
        + " json_extract(events_persisted.payload,'$.data.InFocusDurationMS') AS 'InFocusDurationMS', "
        + " json_extract(events_persisted.payload,'$.data.UserActiveDurationMS') AS 'UserActiveDurationMS', "
        + " json_extract(events_persisted.payload,'$.data.SinceFirstInteractivityMS') AS 'SinceFirstInteractivityMS', "
        + " json_extract(events_persisted.payload,'$.data.UserOrDisplayActiveDurationMS') AS 'UserOrDisplayActiveDurationMS', "
        + " json_extract(events_persisted.payload,'$.data.FocusLostCount') AS 'FocusLostCount',"
        + " CASE WHEN substr(json_extract(events_persisted.payload,'$.data.AppId'),1,1) is 'W'	"
        + "     THEN upper(substr(json_extract(events_persisted.payload,'$.data.AppId'),52,40)) "
        + "     END AS 'SHA1',	"
        + " CASE WHEN substr(json_extract(events_persisted.payload,'$.data.AppId'),1,1) is 'W'	"
        + "    THEN upper(substr(json_extract(events_persisted.payload,'$.data.AppId'),3,44)) "
        + "    END AS 'ProgramId',"
        + " upper(json_extract(events_persisted.payload,'$.data.AppSessionId')) AS 'AppSessionId',"
        + " trim(json_extract(events_persisted.payload,'$.ext.user.localId'),'m:') AS 'UserId',"
        + " sid AS 'UserSID',"
        + " logging_binary_name,"
        + " events_persisted.payload AS JSONPayload"
        + " FROM events_persisted "
        + " JOIN event_tags on events_persisted.full_event_name_hash = event_tags.full_event_name_hash"
        + " JOIN tag_descriptions on event_tags.tag_id = tag_descriptions.tag_id "
        + " where "
        + "  events_persisted.full_event_name in ('Win32kTraceLogging.AppInteractivity','Win32kTraceLogging.AppInteractivitySummary' )"
        + " ORDER BY cast(Timestamp AS integer) DESC";
    
    public static final String DEVICES = "SELECT"
        + " replace(substr(json_extract(events_persisted.payload,'$.time'),1,19), 'T', ' ') as 'UTCTimeStamp',"
        + " datetime((timestamp - 116444736000000000)/10000000, 'unixepoch','localtime') AS 'LocalTimestamp',"
        + " json_extract(events_persisted.payload,'$.ext.loc.tz') AS 'Timezone',"
        + " json_extract(events_persisted.payload,'$.ext.utc.seq') AS 'seq', "
        + " tag_descriptions.tag_name AS TagName,"
        + " replace(events_persisted.full_event_name,'Microsoft.Windows.Inventory.Core.Inventory','') AS 'EventName',"
        + " events_persisted.full_event_name AS 'FullEventName',"
        + " json_extract(events_persisted.payload,'$.data.InstallDate') AS 'InstallDate',"
        + " json_extract(events_persisted.payload,'$.data.FirstInstallDate') AS 'FirstInstallDate',"
        + " json_extract(events_persisted.payload,'$.data.DriverVerDate') AS 'DriverVerDate',"
        + " json_extract(events_persisted.payload,'$.data.Provider') AS 'Provider',"
        + " json_extract(events_persisted.payload,'$.data.Manufacturer') AS 'Manufacturer',"
        + " json_extract(events_persisted.payload,'$.data.Model') AS 'Model',"
        + " coalesce(json_extract(events_persisted.payload,'$.data.ModelName'), json_extract(events_persisted.payload,'$.data.Model'))  as 'Model',"
        + " json_extract(events_persisted.payload,'$.data.BusReportedDescription') AS 'BusDescription',"
        + " json_extract(events_persisted.payload,'$.data.Description') AS 'Description',"
        + " json_extract(events_persisted.payload,'$.data.Enumerator') AS 'Enumerator',"
        + " json_extract(events_persisted.payload,'$.data.baseData.objectInstanceId') AS 'InstanceId',"
        + " trim(json_extract(events_persisted.payload,'$.ext.user.localId'),'m:') AS 'UserId',"
        + " sid AS 'UserSID',"
        + " events_persisted.payload AS JSONPayload"
        + " FROM "
        + "     events_persisted"
        + " LEFT JOIN event_tags ON events_persisted.full_event_name_hash = event_tags.full_event_name_hash"
        + " LEFT JOIN tag_descriptions ON event_tags.tag_id = tag_descriptions.tag_id"
        + " WHERE "
        + " FullEventName LIKE 'Microsoft.Windows.Inventory.Core.InventoryDevice%'"
        + " ORDER BY cast(Timestamp AS integer) DESC;";

    public static final String CENSUS = "SELECT"
        + " replace(substr(json_extract(events_persisted.payload,'$.time'),1,19), 'T', ' ') as 'UTCTimeStamp',"
        + " datetime((timestamp - 116444736000000000)/10000000, 'unixepoch','localtime') AS 'LocalTimestamp',"
        + " json_extract(events_persisted.payload,'$.ext.loc.tz') AS 'Timezone',"
        + " json_extract(events_persisted.payload,'$.ext.utc.seq') AS 'seq',"
        + " replace(events_persisted.full_event_name,'Census.','') AS 'EventName',"
        + " tag_descriptions.tag_name AS TagName,"
        + " json_extract(events_persisted.payload,'$.data') AS 'State \\ Settings',"
        + " trim(json_extract(events_persisted.payload,'$.ext.user.localId'),'m:') AS 'UserId',"
        + " sid AS 'UserSID',"
        + " logging_binary_name,"
        + " events_persisted.payload AS JSONPayload"
        + " FROM events_persisted "
        + " JOIN event_tags on events_persisted.full_event_name_hash = event_tags.full_event_name_hash"
        + " JOIN tag_descriptions on event_tags.tag_id = tag_descriptions.tag_id "
        + " WHERE "
        + "   events_persisted.full_event_name LIKE 'Census%' "
        + " ORDER BY cast(events_persisted.timestamp AS integer) DESC";

    public static final String NETWORKING = " SELECT"
        + " replace(substr(json_extract(events_persisted.payload,'$.time'),1,19), 'T', ' ') as 'UTCTimeStamp',"
        + " datetime((timestamp - 116444736000000000)/10000000, 'unixepoch','localtime') AS 'LocalTimestamp',"
        + " json_extract(events_persisted.payload,'$.ext.loc.tz') AS 'Timezone',"
        + " json_extract(events_persisted.payload,'$.ext.utc.seq') AS 'seq',"
        + " replace(replace(replace(replace(replace(replace(full_event_name,'Microsoft.OneCore.NetworkingTriage.GetConnected.',''),'Microsoft.Windows.Networking.DHCP.',''),'Microsoft.Windows.Networking.DHCPv6.',''),'Microsoft.Windows.Networking.DNS.',''),'Microsoft.Windows.Networking.SharedAccess.',''),'Microsoft.Windows.Networking.NetworkSetupSvc.','') AS 'Event',"
        + " json_extract(events_persisted.payload,'$.data.eventSource') AS 'EventSource',"
        + " coalesce(json_extract(events_persisted.payload,'$.data.reason'),json_extract(events_persisted.payload,'$.data.eventSource')) AS 'EventReason',"
        + " json_extract(events_persisted.payload,'$.data.previousReason') AS 'Previous Reason',"
        + " json_extract(events_persisted.payload,'$.data.nextHopAddress') AS 'nextHopAddress',"
        + " coalesce(json_extract(events_persisted.payload,'$.data.apPhyType'),json_extract(events_persisted.payload,'$.data.family')) AS 'Type',"
        + " CASE "
        + "     WHEN json_extract(events_persisted.payload,'$.data.interfaceType') in (null,'Other') AND json_extract(events_persisted.payload,'$.data.selectedIconAltText') is null AND json_extract(events_persisted.payload,'$.data.mode') is null"
        + "     then json_extract(events_persisted.payload,'$.data.launchType')"
        + "     WHEN json_extract(events_persisted.payload,'$.data.interfaceType') in (null,'Other') AND json_extract(events_persisted.payload,'$.data.selectedIconAltText') is null "
        + "     then json_extract(events_persisted.payload,'$.data.mode')"
        + "     ELSE json_extract(events_persisted.payload,'$.data.interfaceType')"
        + "     END  AS 'interfaceType',"
        + " json_extract(events_persisted.payload,'$.data.profileName') AS 'profileName',"
        + " coalesce(json_extract(events_persisted.payload,'$.data.selectedIconAltText'),json_extract(events_persisted.payload,'$.data.networkStatus')) AS 'Icon/Status',"
        + " json_extract(events_persisted.payload,'$.data.networkCategory') AS 'networkCategory',"
        + " json_extract(events_persisted.payload,'$.data.apManufacturer') AS 'apManufacturer',"
        + " json_extract(events_persisted.payload,'$.data.apModelName') AS 'apModelName',"
        + " json_extract(events_persisted.payload,'$.data.apModelNum') AS 'apModelNum',"
        + " json_extract(events_persisted.payload,'$.data.cipherAlgo') AS 'EncrType',"
        + " json_extract(events_persisted.payload,'$.data.bssid') AS 'bssid',"
        + " json_extract(events_persisted.payload,'$.data.firstBSSID') AS 'firstBSSID',"
        + " json_extract(events_persisted.payload,'$.data.DhcpMode') AS 'DhcpMode', "
        + " CASE json_extract(events_persisted.payload,'$.data.GotOffer') "
        + "     WHEN 0 then 'No'"
        + "     WHEN 1 then 'Yes'"
        + "     ELSE json_extract(events_persisted.payload,'$.data.GotOffer')"
        + "     END AS 'GotOffer', "
        + " CASE json_extract(events_persisted.payload,'$.data.DisableDhcpSet') "
        + "     WHEN 0 then 'No'"
        + "     WHEN 1 then 'Yes'"
        + "     END AS 'DisableDhcpSet', 	"
        + " CASE json_extract(events_persisted.payload,'$.data.DhcpIsInitState') "
        + "     WHEN 0 then 'No'"
        + "     WHEN 1 then 'Yes'"
        + "     ELSE json_extract(events_persisted.payload,'$.data.DhcpIsInitState')"
        + "     END AS 'DhcpIsInitState', "
        + " CASE json_extract(events_persisted.payload,'$.data.DhcpGlobalUseNetworkHint') "
        + "     WHEN 0 then 'No'"
        + "     WHEN 1 then 'Yes'"
        + "     END AS 'DhcpGlobalUseNetworkHint', "
        + "     CASE json_extract(events_persisted.payload,'$.data.LeaseObtained')"
        + "         WHEN 0 then 'No'"
        + "         ELSE time(json_extract(events_persisted.payload,'$.data.LeaseObtained'),'unixepoch')"
        + "         END AS 'LeaseObtained',"
        + " time(json_extract(events_persisted.payload,'$.data.LeaseTime'),'unixepoch') AS 'LeaseTime',"
        + " json_extract(events_persisted.payload,'$.data.NextHop') AS 'NextHop',"
        + " json_extract(events_persisted.payload,'$.data.AssignedAddress') AS 'AssignedAddress',"
        + " json_extract(events_persisted.payload,'$.data.Dest') AS 'Dest',"
        + " json_extract(events_persisted.payload,'$.data.DestMask') AS 'DestMask',"
        + " json_extract(events_persisted.payload,'$.data.DnsServers') AS 'DnsServers',"
        + " json_extract(events_persisted.payload,'$.data.interfaceDescription') AS 'Interface',"
        + " json_extract(events_persisted.payload,'$.data.interfaceGuid') AS 'interfaceGuid',"
        + " json_extract(events_persisted.payload,'$.data.SessionTrackingGuid') AS 'Session',"
        + " json_extract(events_persisted.payload,'$.data.driverDesc') AS 'driverDesc',"
        + " json_extract(events_persisted.payload,'$.data.pnpId') AS 'pnpId',"
        + " json_extract(events_persisted.payload,'$.data.providerName') AS 'providerName',"
        + " json_extract(events_persisted.payload,'$.data.mediaType') AS 'mediaType',"
        + " json_extract(events_persisted.payload,'$.data.physicalMediaType') AS 'physicalMediaType',"
        + " trim(json_extract(events_persisted.payload,'$.ext.user.localId'),'m:') AS 'UserId',"
        + " sid AS 'UserSID',"
        + " json_extract(events_persisted.payload,'$.data') AS 'dataJSON',"
        + " events_persisted.payload AS JSONPayload,"
        + " logging_binary_name"
        + " FROM events_persisted "
        + " WHERE "
        + "   (events_persisted.full_event_name LIKE 'Microsoft.OneCore.NetworkingTriage.%' "
        + " or events_persisted.full_event_name LIKE 'Microsoft.Windows.Networking.SharedAccess.%'"
        + " or events_persisted.full_event_name LIKE 'Microsoft.Windows.Networking.DHCP%'"
        + " or events_persisted.full_event_name LIKE 'Microsoft.Windows.Networking.DNS.DnsServerConfig.%'"
        + " or events_persisted.full_event_name LIKE 'Microsoft.Windows.Networking.NetworkSetupSvc.InstallPnPDevice%'"
        + " or events_persisted.full_event_name LIKE 'Microsoft.Windows.Networking.NetworkSetupSvc.ForeignNetworkInterface%')"
        + " AND events_persisted.full_event_name not LIKE '%DiscoveryAttempt%'"
        + " AND events_persisted.full_event_name not LIKE '%MediaConnected%'"
        + " AND events_persisted.full_event_name not LIKE '%DhcpSetEventInRenewState%'"
        + " AND events_persisted.full_event_name not LIKE '%SolicitAttempt%'"
        + " AND events_persisted.full_event_name not LIKE '%InterfaceCapabilityChangedEvent%'"
        + " ORDER BY cast(events_persisted.timestamp AS integer) DESC";
}
