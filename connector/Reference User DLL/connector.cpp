#define USER_DLL

#include "user.h"
#include <string>
#include <string.h>
#include <sstream>
#include <iomanip>
#include "stdio.h"

#include <windows.h>
#include <algorithm>
#include <iterator>
#include <process.h>
#include <vector>
#include <set>
#include <fstream>
#include <bitset>
#include <ostream>
#include "jansson.h"
#include "http.h"
#include "AllPPLSymbols.h"
#include "boost/uuid/uuid.hpp"
#include "boost/uuid/random_generator.hpp"
#include "boost/uuid/uuid_io.hpp"
#include "boost/filesystem.hpp"
#include "boost/iostreams/device/file.hpp"
#include "boost/iostreams/stream.hpp"
#include "boost/algorithm/string.hpp"

using namespace std;
//#define Log printf
//#define Log WriteLog
#define Log pblog

// Forward declarations
void init_curl();
void clean_curl();
int request(const char *url, char* buffer, unsigned int buffer_size);
int is_openppl_command(const char* pquery);
void clear_hand_images();
int is_heartbeat(const char* pquery);
int is_new_round(const char* pquery);
bool is_OH_betround_bug(const char* pquery);

HANDLE symbol_need, symbol_ready, have_result;

std::set<std::string> all_symbol_names;

bool connector_enabled = false;
struct ext_holdem_player {
	bool playing;
	bool active;
	bool dealt;
	bool blind;
	bool cards_shows;
};

struct ext_holdem_state {
	const char* datatype;
	int current;
  int hand_count;
	int hand_error;
	int first_hand;
	int my_turn_count;
	bool passed_preflop;           // true if the hand passed preflop betround 
	int snapshot_count;
	std::string hand_uuid;
  int dll_listening_port;
	std::string betround;
  ext_holdem_player   player[10]        ;       //player records
	std::string current_query;

	// player actions 
	string play_action;
	int    play_raise;
	string play_betround;
};
struct ext_holdem_state ext_state;

double client_result;

boost::uuids::random_generator uuid_generator;

#define STATE_INDEX ((state_index-1)&0xFF)
#define CURRENT_STATE (state[STATE_INDEX])
#define CARD_VALID(card) (card<0xFE)

struct configuration_st {
	string server_url;
	string offline;
	string clear_on_restart;
} configuration;

#define BUF_SIZE (256*1024)
void pblog(const char* fmt,...) {
	char buffer[BUF_SIZE];
	va_list args;
    va_start(args, fmt);
	vsprintf_s(buffer, BUF_SIZE, fmt, args);
	va_end(args);
	WriteLog(buffer);
	printf(buffer);
}

void init_all_symbols_names() {
	char *excluded[] = {
			"f$debug", 
			"dll",
			"f",
			"f$allin_on_betsize_balance_ratio",
			"f$ini_function_on_startup",
			"f$ini_function_on_connection",
			"f$ini_function_on_handreset",
			"f$ini_function_on_new_round",
			"f$ini_function_on_my_turn",
			"f$ini_function_on_heartbeat",
			"f$test",
			"f$preflop",
			"f$flop",
			"f$turn",
			"f$river",
			"log$",
			"msgbox$",
			"notes",
			"HaveBackdoorstraightDraw",
			"HaveBackdoorStraightDraw",

			"icm_allilose1", // crashes ?!?
			"icm_allilose2", // crashes ?!?
			"icm_allilose3", // crashes ?!?
			"icm_allilose4", // crashes ?!?
			"icm_allilose5", // crashes ?!?
			"icm_allilose6", // crashes ?!?
			"icm_allilose7", // crashes ?!?
			"icm_allilose8", // crashes ?!?
			"icm_allilose9", // crashes ?!?
			
			// TODO - fix in OH and pull request
			"icm_allilosCO", 
			"icm_allilosD", 
			"icm_allilosBB",
			"icm_allilosSB",
			"icm_allilosUTG",
			"icm_allilosUTG1",
			"icm_allilosUTG2", 
			"icm_allilosUTG3",
			"icm_allilosUTG4",
			"icm_allilosUTG5",
			"icm_allilosUTG6",
			"nchairsdealt",
			"pl_",
			"vs",
			"vs$",
			"vs$list",
			"vs$multiplex$",

			""
	};
  const char* all_symbols_string = GetAllSymbols();
	all_symbol_names.clear();
	std::set<std::string> all_symbols;
	boost::split(all_symbols, all_symbols_string, boost::is_any_of(" "));
  std::set<std::string> excluded_set(excluded, excluded + (sizeof(excluded) / sizeof(excluded[0])));
	std::set_difference(all_symbols.begin(), all_symbols.end(), excluded_set.begin(), excluded_set.end(), 
			            std::inserter(all_symbol_names, all_symbol_names.begin()));
	all_symbol_names.erase("");
	all_symbol_names.erase(all_symbol_names.lower_bound("pt_"), all_symbol_names.upper_bound("pt_z"));
}

string num_to_card(unsigned char num) 
{
    if (!CARD_VALID(num)) return "--";
    int card = (num%13)+2;
    char cs, cn;
    switch (card){
        case 14: cn = 'A'; break;
        case 10: cn = 'T'; break; 
        case 11: cn = 'J'; break;
        case 12: cn = 'Q'; break;
        case 13: cn = 'K'; break;   
        default: cn = card + '0';
    }

    switch (num/13) {
        case 0: cs = 'h'; break;
        case 1: cs = 'd'; break;
        case 2: cs = 'c'; break;
        case 3: cs = 's'; break;
        default: cs = 'E'; break;
    }	    	
    ostringstream os;
    os << cn << cs ;
    return os.str();
}

string holdem_player_to_string(int num)
{
	holdem_player* player = &CURRENT_STATE.m_player[num];
	ext_holdem_player* ext_player = &(ext_state.player[num]);
    ostringstream os;
	os  
		<< "#" << std::to_string((long double)num) 
        << "|stt:"
		<< (ext_player->blind?"B":"-")
		<< (ext_player->active?"A":"-")
		<< (ext_player->dealt?"D":"-")
		<< (ext_player->playing?"P":"-")
        << "|bal:" << std::setw(4) << std::setfill(' ') << (player->m_balance_known ? (int)player->m_balance : -1)
        << "|bet:" << std::setw(4) << std::setfill(' ') << player->m_currentbet 
        << "|cards:" << num_to_card(player->m_cards[0]) << num_to_card(player->m_cards[1])
        << "|name:" << (player->m_name_known ? (char*)player->m_name : "unkown");
    return os.str();
}

string holdem_state_to_string()
{
  ostringstream os;
  os << ext_state.hand_uuid 
     << "|betround:" << ext_state.betround
     << "|board:";
  for (int i=0; i<5; i++) {
    os << num_to_card(CURRENT_STATE.m_cards[i]);
    if (i!=4) os << ",";
  }
  os << "|pot:";
  for (int i=0; i<10; i++) {
    os << (int)state->m_pot[i];
    if (i!=9) os << ",";
  }
  os << (state->m_is_playing?"|play":"|not play")
     << (state->m_is_posting?"|post":"|not post") 
	   << "|sttidx:" << std::to_string((long double)STATE_INDEX)
     << "|deal:" << (int)CURRENT_STATE.m_dealer_chair;
	os << "|query:" << ext_state.current_query;
  os << "\n";
  for (int i=0; i<10; i++) {
    if (state->m_player[i].m_name_known == 0)
      continue;
    os << "    " << holdem_player_to_string(i) << "\n";
  }

  return os.str();
}

json_t* json_holdem_player(holdem_player* player, ext_holdem_player* ext_player) {
	// build hand card array
	json_t *cards = json_array();
	for (int i=0; i<2; i++)
		json_array_append_new(cards, json_string(num_to_card(player->m_cards[i]).c_str()));
	
  // build main json
	json_t *main = json_object();
  json_object_set_new(main, "name", json_string(player->m_name));
	json_object_set_new(main, "balance", json_real(player->m_balance));
	json_object_set_new(main, "currentbet", json_real(player->m_currentbet));
	json_object_set_new(main, "cards", cards);
	json_object_set_new(main, "name_known", json_integer(player->m_name_known));
	json_object_set_new(main, "balance_known", json_integer(player->m_balance_known));
	json_object_set_new(main, "fillerbits", json_integer(player->m_fillerbits));
	json_object_set_new(main, "fillerbytes", json_integer(player->m_fillerbyte));

	json_object_set_new(main, "playing", json_integer(ext_player->playing));
	json_object_set_new(main, "blind", json_integer(ext_player->blind));
	json_object_set_new(main, "active", json_integer(ext_player->active));
	json_object_set_new(main, "dealt", json_integer(ext_player->dealt));
	json_object_set_new(main, "cards_shows", json_integer(ext_player->cards_shows));

	return main;
}

string get_current_betround() {
	double betround = GetSymbol("betround");
	switch ((int)betround) {
	case 1 : return "preflop";
	case 2 : return "flop";
	case 3 : return "turn";
	case 4 : return "river";
	default : return "unknown";
	}
}

json_t* holdem_state_to_json(holdem_state* state) {
	// build hand matadata
	json_t *jstate = json_object();
	json_object_set_new(jstate, "datatype",     json_string(ext_state.datatype));
	json_object_set_new(jstate, "room",			json_string(state->m_title));
	json_object_set_new(jstate, "table",		0);
	json_object_set_new(jstate, "handcount",	json_integer(ext_state.hand_count));
	json_object_set_new(jstate, "uuid",			json_string(ext_state.hand_uuid.c_str()));
	json_object_set_new(jstate, "betround",		json_string(ext_state.betround.c_str()));
	json_object_set_new(jstate, "title",		json_string((char*)state->m_title));
	json_object_set_new(jstate, "is_playing",	json_integer(state->m_is_playing));
	json_object_set_new(jstate, "is_posting",	json_integer(state->m_is_posting));
	json_object_set_new(jstate, "fillerbits",	json_integer(state->m_fillerbits));
	json_object_set_new(jstate, "dealer_chair", json_integer(state->m_dealer_chair));
	json_object_set_new(jstate, "my_turn_count", json_integer(ext_state.my_turn_count));

	// build the pots array
	json_t *pots = json_array();
	for (int i=0; i<10; i++) 
		json_array_append_new(pots, json_real(state->m_pot[i]));

	// build the cards array
	json_t *cards = json_array();
	for (int i=0; i<5; i++)
		json_array_append_new(cards, json_string(
		    num_to_card(state->m_cards[i]).c_str()));

	// add players
	json_t *players = json_array();
	for (int i=0; i<10; i++)
		json_array_append_new(players, json_holdem_player(&state->m_player[i], &ext_state.player[i]));

	// add symbols
  json_t *symbols = json_object();
	int idx = 0;
	std::set<std::string>::iterator it;
  for (it = all_symbol_names.begin(); it != all_symbol_names.end(); ++it) {
    std::string sym = *it; 
	  double val = GetSymbol(sym.c_str());
	  json_object_set_new(symbols, sym.c_str(), json_real(val));
	}

	idx = 0;
  double init = GetSymbol("TimeToInitMemorySymbols");
	GetSymbol("InitMemorySymbols"); // Initialize OpenPPL memory symbols
	const char* sym = 0;
	while (*(sym = all_ppl_symbol_names[idx++]) != '\0') {
		if ((string(sym) == "HaveBackdoorstraightDraw") ||
        (string(sym) == "HaveBackdoorStraightDraw"))
				continue;
		double val = GetSymbol(sym);
		json_object_set_new(symbols, sym, json_real(val));
	}
	
	// build the main object
  json_t *main = json_object();
	json_object_set_new(main, "state", jstate);
	json_object_set_new(main, "cards", cards);
	json_object_set_new(main, "pots", pots);
  json_object_set_new(main, "players", players);
	json_object_set_new(main, "symbols", symbols);

  return main;
}

string holdem_state_to_json_string(holdem_state* state)
{
	return json_dumps(holdem_state_to_json(state), JSON_INDENT(4));
}

namespace io = boost::iostreams;
void write_error_to_captures(std::string file, int error_code, std::string error, std::string url, std::string request)
{
	std::ofstream outfile(file);
	if (!outfile.is_open()) {
		Log("Failed to open file for write %s\n",file.c_str());
		return;
	}

    outfile << "code:" << error_code << "\n";
	outfile << "url:" << url << "\n";
	outfile << "error:\n" << error << "\n";
	outfile << "request:\n" << request << "\n";
	outfile.close();
}

void send_table_state() {
	if (ext_state.hand_error) {
    Log("Hand in error state, avoid sending:%s, handnumber:%d, betround:%s\n", 
				ext_state.datatype, 
				ext_state.hand_count,
				ext_state.betround.c_str());
		return;
	}
  Log("sending:%s, handnumber:%d, betround:%s\n", 
				ext_state.datatype, 
				ext_state.hand_count,
				ext_state.betround.c_str());
	string data = holdem_state_to_json_string(&state[STATE_INDEX]);
	string url = configuration.server_url
					+ "/tablestate/" 
					+ ext_state.hand_uuid
					+ "/"
					+ ext_state.datatype;
	char response[1024];
	if (configuration.offline == "yes")
		return;
	int rescode = http_post(url.c_str(), data.c_str(), (char*)response, 1024); 
	std::string error;
	if (rescode/100 != 2) { // checks for status 2XX
		ext_state.hand_error = true;
	    error="-error";
	}
	std::string filename = "captures\\"+ext_state.hand_uuid+"\\"+std::to_string((long double)ext_state.snapshot_count)+error+".txt";
	write_error_to_captures(filename, rescode, response, url, data);
}

bool is_OH_betround_bug(const char* pquery) 
{
	// OH has a bug that it changes the betround before handreset is changed , happens on new_round and heartbeat events 
	if ((ext_state.passed_preflop) && 
		((is_heartbeat(pquery) || is_new_round(pquery))) &&
		(ext_state.betround == "preflop"))
		return true;
	return false;
}

int is_showdown() {
	holdem_state* cstate = &state[STATE_INDEX];
	// Check first if river
    if (string(ext_state.betround) != "river")
		return 0;

	// Check that players other than me are showing cards
	int user_chair = (int)GetSymbol("userchair");
	ext_holdem_player* player = 0;
	bool found_one = false;
	for (int i=0; i<10; i++) {
		if (i==user_chair)
			continue;
		if (!ext_state.player[i].playing)
			continue;
		if (ext_state.player[i].cards_shows)
			continue;
		return false;
	}
	return true;
}

int do_showdown() 
{
	ext_state.datatype = "showdown";
	//Log("showdown\n");
  send_table_state();
	return 0;
}

int is_heartbeat(const char* pquery) 
{
	if (string(pquery) == "dll$ini_function_on_heartbeat") 
		return 1;
	return 0;
}

int do_heartbeat(const char* pquery)
{
	ext_state.datatype = "heartbeat";
//	if (!is_OH_betround_bug(pquery)) {
//		send_table_state();
//	}
	return 0;
}

int is_playing()
{
    holdem_state* cstate = &state[STATE_INDEX];
	return (cstate->m_is_playing);
}

void do_not_playing()
{
	//Log("not playing\n");
}

int is_hand_error() {
	return ext_state.hand_error;
}

int do_hand_error(const char* pquery) {
	if (is_openppl_command(pquery))
		return 0; // fold anything
	return 0;
}

int is_hand_reset(const char* pquery) 
{
    if (string(pquery) != "dll$ini_function_on_handreset")
	    return 0;	
	return 1;
}

void init_new_hand()
{
	ext_state.hand_uuid = boost::uuids::to_string(uuid_generator());
	boost::filesystem::create_directory("captures//"+ext_state.hand_uuid);
	ext_state.hand_count++;
	ext_state.snapshot_count	= 0;
	ext_state.hand_error			= false;
	ext_state.passed_preflop	= false;
	ext_state.play_betround		= "";
	ext_state.my_turn_count		= -1;
}

int do_hand_reset() 
{
  ext_state.datatype = "posthand";
	send_table_state();
	if (!is_hand_error())
		clear_hand_images();
	init_new_hand();
	ext_state.datatype = "handreset";
	Log("Handreset:%d, UUID:%s\n", ext_state.hand_count, ext_state.hand_uuid.c_str());
	send_table_state();

	return 0;
}

int is_new_round(const char* pquery)
{
	if (string(pquery) == "dll$ini_function_on_new_round")
	{
		//Log("On new round : %s\n",ext_state.betround.c_str());
		return 1;
	}
	return 0;
}

int do_new_round(const char* pquery)
{
	ext_state.datatype = "new_round";
	if (!is_OH_betround_bug(pquery))
		send_table_state(); // bug in OH, sometime sends new round preflop before handreset
	return 0;
}

int is_my_turn(const char* pquery) 
{
    return (string(pquery) == "dll$ini_function_on_my_turn");
}

bool read_play_response(const char* response) 
{
	json_t *json;
	json_error_t error;

	json = json_loadb(response, strlen(response), 0, &error);
	if(!json) {
		Log("failed to parse play response\n");
    return false;	
	}

	json_t* jstatus = json_object_get(json, "status");
  json_t* jaction = json_object_get(json,"action");
  json_t* jraise  = json_object_get(json,"raise");
	if ((!json_is_string(jstatus)) || (!json_is_string(jaction)) || (!json_is_real(jraise)))  {
		Log("Invalid or missing value in play response : %s\n",response);
		return false;
	}
	const char* status = json_string_value(jstatus);
	const char* action = json_string_value(jaction);
	double raise  = json_real_value(jraise);
	
	ext_state.play_betround = ext_state.betround; 
	ext_state.play_action   = action;
	ext_state.play_raise    = (int)raise;

	Log("playing: betround %s, action %s, raise %d\n",
			ext_state.play_betround.c_str(), 
			ext_state.play_action.c_str(),
			ext_state.play_raise); 
	if (string(status) != "ok") {
		Log("Server returned failed status:%s\n",status);
		return false;
	}

	return true;
}


bool read_play_action() 
{
	char response[1024*2];
	string url = configuration.server_url
					+ "/play/"
					+ ext_state.hand_uuid
					+ '/'
					+ ext_state.betround;
	if (configuration.offline == "yes")
		return true;
	int rescode = http_get(url.c_str(), (char*)response, 1024); 
	std::string error = "play";
	if (rescode/100 != 2) { // checks for status 2XX
		ext_state.hand_error = true;
	  error="-play-error";
	}
	std::string filename = "captures\\"+ext_state.hand_uuid+"\\"+std::to_string((long double)ext_state.snapshot_count)+error+".txt";
	write_error_to_captures(filename, rescode, response, url, "");
	if (ext_state.hand_error)
			return false;
	return read_play_response(response);
}

int do_my_turn()
{
	return 0;
}

int is_openppl_command(const char* pquery) {

	if (string(pquery).find("dll$player_") == 0) {
		return true;
	}

	return false;
}

int do_openppl_command(const char* pquery)
{
	string q = pquery;
	if (ext_state.hand_error)
		return 0; // fold on error 
	if (configuration.offline == "yes") {
			if ((q == "dll$player_call") || (q == "dll$player_check"))
					return 1;
			return 0;
	}

	static bool action_executed = true;
	// Skip play request if already requested for this betround
	if (action_executed) {
		ext_state.datatype = "my_turn";
		ext_state.my_turn_count++;
		send_table_state();
		if (read_play_action())
      action_executed = false;
	}

	if ((q == "dll$player_allin") && (ext_state.play_action == "allin")) {
		action_executed = true;
		return 1;
	}

	if ((q == "dll$player_raise") && (ext_state.play_action == "raise"))
		return 1;

	if ((q == "dll$player_call") && (ext_state.play_action == "call")) {
		action_executed = true;
		return 1;
	}

	if ((q == "dll$player_check") && (ext_state.play_action == "check")) {
		action_executed = true;
		return 1;
  }

	if ((q == "dll$player_fold") && (ext_state.play_action == "fold")) {
		action_executed = true;
		return 1;
	}

	if ((q == "dll$player_raise_to") && (ext_state.play_action == "raise")) {
		action_executed = true;
	  int bblind = (int)GetSymbol("bblind");
		return ext_state.play_raise / bblind;
	}

	return 0;
}

void fill_extended_table_state(const char* pquery) 
{
	int playingbits = (int)GetSymbol("playersplayingbits");
	int blindbits = (int)GetSymbol("playersblindbits");
	int activebits = (int)GetSymbol("playersactivebits"); 
	int dealtbits = (int)GetSymbol("playersdealtbits");

  holdem_state* cstate = &state[STATE_INDEX];
	ext_state.betround = get_current_betround();
	if (ext_state.betround != "preflop")
    ext_state.passed_preflop = true;
	ext_state.snapshot_count++;
	ext_state.current_query = pquery;
	for (int i=0; i<10; i++) {
		ext_state.player[i].playing     = (playingbits&(0x1<<i))==0?false:true;
		ext_state.player[i].blind       = (blindbits&(0x1<<i))==0?false:true;
		ext_state.player[i].active      = (activebits&(0x1<<i))==0?false:true;
		ext_state.player[i].dealt       = (dealtbits&(0x1<<i))==0?false:true;
		ext_state.player[i].cards_shows = 
					((ext_state.player[i].playing) &&
					 (CARD_VALID(cstate->m_player[i].m_cards[0])) && 
					 (CARD_VALID(cstate->m_player[i].m_cards[1])));
	}
	//Log(holdem_state_to_string().c_str());
}

int is_table_found() {
  holdem_state* cstate = &state[STATE_INDEX];
	return (strlen(cstate->m_title) !=0 );
}

int CaptureAnImage(HWND hWnd, std::string filename);
int ClearImages(std::string folder);
void save_snapshot_image() 
{
	std::string filename = "captures\\"+ext_state.hand_uuid+"\\"+std::to_string((long double)ext_state.snapshot_count)+".bmp";
	int int_hwnd =(int)GetSymbol("attached_hwnd"); 
	HWND hwnd = (HWND)int_hwnd;
	CaptureAnImage(hwnd, filename);
}

void clear_hand_images() 
{
	if (ext_state.hand_uuid == "")
		return;
	std::string folder = "captures\\"+ext_state.hand_uuid;
	boost::filesystem::remove_all(folder);
}

/*
 * Handler for OH query message.
 */
double process_query(const char* pquery)
{
	if (!is_table_found())
		return 0;
	fill_extended_table_state(pquery);
	save_snapshot_image();
	if (is_hand_reset(pquery)) 
		return do_hand_reset();

	if (is_hand_error()) 
    return do_hand_error(pquery);

	if (is_new_round(pquery))
		return do_new_round(pquery);

	if (is_heartbeat(pquery)) {
		do_heartbeat(pquery);
		if (!is_playing())
			do_not_playing(); 
		if (is_showdown()) 
			do_showdown();
		return 0;
	}

	if (is_my_turn(pquery))
		return do_my_turn();

	if (is_openppl_command(pquery))
		return do_openppl_command(pquery);

  return 0;
}

/*
 * Handler for OH state message.
 */
double process_state(holdem_state* pstate)
{
	return 0;
}

bool read_config() 
{
	json_t *json;
	json_error_t error;

	json = json_load_file("pb_config.json", 0, &error);
	if(!json) {
		Log("failed to load config file\n");
	    return false;	
	}
	WriteLog("test %s:\n","This is a test 123");
    Log("config file loaded\n");	
	
    json_t* url = json_object_get(json,"server_url");
	if (json_is_string(url)) {
    	configuration.server_url = json_string_value(url);
		Log("Server URL:%s\n",configuration.server_url.c_str());
	}
	json_t* offline = json_object_get(json, "offline");
	if (json_is_string(offline)) {
		configuration.offline = json_string_value(offline);
		Log("Server is offline? - %s\n",configuration.offline.c_str());
	}
	json_t* clear_on_restart = json_object_get(json, "clear_on_restart");
	if (json_is_string(clear_on_restart)) {
		configuration.clear_on_restart = json_string_value(clear_on_restart);
		Log("Clear on restart? - %s\n",configuration.clear_on_restart.c_str());
	}
	return true;
}

void clear_on_restart() 
{
	if (configuration.clear_on_restart == "yes") {
		boost::filesystem::remove_all("captures");
	}
	boost::filesystem::create_directory("captures");
}

void init_connector()
{
	init_curl();
	if (!read_config())
		return;

	clear_on_restart();
  init_new_hand();
	init_all_symbols_names();
	connector_enabled = true;
}

void clean_connector()
{
	clean_curl();
}

bool is_connector_enabled() 
{
	return connector_enabled;
}