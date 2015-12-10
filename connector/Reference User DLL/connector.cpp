#define USER_DLL

#include "user.h"
#include <string>
#include <string.h>
#include <sstream>
#include <iomanip>
#include "stdio.h"

#include <windows.h>
#include <process.h>
#include <vector>
#include <fstream>
#include <bitset>
#include <ostream>
#include "jansson.h"
#include "http.h"
#include "AllSymbols.h"
#include "AllPPLSymbols.h"
#include "boost/uuid/uuid.hpp"
#include "boost/uuid/random_generator.hpp"
#include "boost/uuid/uuid_io.hpp"
#include "boost/filesystem.hpp"
#include "boost/iostreams/device/file.hpp"
#include "boost/iostreams/stream.hpp"

using namespace std;
//#define Log printf
//#define Log WriteLog
#define Log pblog
void init_curl();
void clean_curl();

// Forward declarations
int request(const char *url, char* buffer, unsigned int buffer_size);
int is_openppl_command(const char* pquery);
void clear_hand_images();

HANDLE symbol_need, symbol_ready, have_result;

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
	int snapshot_count;
	std::string hand_uuid;
    int dll_listening_port;
	std::string betround;
    ext_holdem_player   player[10]        ;       //player records
	std::string current_query;
};
struct ext_holdem_state ext_state;

double client_result;

boost::uuids::random_generator uuid_generator;

#define STATE_INDEX ((state_index-1)&0xFF)
#define CURRENT_STATE (state[STATE_INDEX])
#define CURRENT_EXT_STATE ext_state
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
	ext_holdem_player* ext_player = &(CURRENT_EXT_STATE.player[num]);
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
		<< "|betround:" << CURRENT_EXT_STATE.betround
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
	os << "|query:" << CURRENT_EXT_STATE.current_query;
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
	const char* sym = 0;
	int idx = 0;
	while (*(sym = all_symbol_names[idx++]) != '\0') {
		// excluded symbols (TODOs)
		if (sym == "icm_allilose1") // crashes
			continue;
	    double val = GetSymbol(sym);
	    json_object_set_new(symbols, sym, json_real(val));
	}

	json_t *ppl_symbols = json_object();
	idx = 0;
	while (*(sym = all_ppl_symbol_names[idx++]) != '\0') {
		double val = GetSymbol(sym);
		json_object_set_new(ppl_symbols, sym, json_real(val));
	}
	
	// build the main object
    json_t *main = json_object();
	json_object_set_new(main, "state", jstate);
	json_object_set_new(main, "cards", cards);
	json_object_set_new(main, "pots", pots);
    json_object_set_new(main, "players", players);
	json_object_set_new(main, "symbols", symbols);
	json_object_set_new(main, "ppl_symbols", ppl_symbols);

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
					+ ext_state.hand_uuid
					+ '/'
					+ ext_state.datatype;
	char response[1024];
	if (configuration.offline == "yes")
		return;
	int rescode = post(url.c_str(), data.c_str(), (char*)response, 1024); 
	std::string error;
	if (rescode/100 != 2) { // checks for status 2XX
		ext_state.hand_error = true;
	    error="-error";
	}
	std::string filename = "captures\\"+ext_state.hand_uuid+"\\"+std::to_string((long double)ext_state.snapshot_count)+error+".txt";
	write_error_to_captures(filename, rescode, response, url, data);
}

int is_showdown() {
	holdem_state* cstate = &state[STATE_INDEX];
	// Check first if river
    if (string(CURRENT_EXT_STATE.betround) != "river")
		return 0;

	// Check that players other than me are showing cards
	int user_chair = (int)GetSymbol("userchair");
	ext_holdem_player* player = 0;
	bool found_one = false;
	for (int i=0; i<10; i++) {
		if (i==user_chair)
			continue;
		if (!CURRENT_EXT_STATE.player[i].playing)
			continue;
		if (CURRENT_EXT_STATE.player[i].cards_shows)
			continue;
		return false;
	}
	return true;
}

int do_showdown() 
{
	ext_state.datatype = "showdown";
	Log("showdown\n");
    send_table_state();
	return 0;
}

int is_heartbeat(const char* pquery) 
{
	if (string(pquery) == "dll$ini_function_on_heartbeat") 
		return 1;
	return 0;
}

int do_heartbeat()
{
	ext_state.datatype = "heartbeat";
	send_table_state();
	return 0;
}

int is_playing()
{
    holdem_state* cstate = &state[STATE_INDEX];
	return (cstate->m_is_playing);
}

void do_not_playing()
{
	Log("not playing\n");
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
	ext_state.snapshot_count = 0;
	ext_state.hand_error = false;
}

int do_hand_reset() 
{
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
		Log("On new round : %s\n",ext_state.betround.c_str());
		return 1;
	}
	return 0;
}

int do_new_round()
{
	ext_state.datatype = "new_round";
	send_table_state(); // bug in OH, sometime sends new round preflop before handreset
	return 0;
}

int is_my_turn(const char* pquery) 
{
    return (string(pquery) == "dll$ini_function_on_my_turn");
}

int do_my_turn()
{
	ext_state.datatype = "my_turn";
	send_table_state();
	return 0;
}

int is_openppl_command(const char* pquery) {

	if (string(pquery).find("dll$preflop_") == 0) {
		return true;
	}

	if (string(pquery).find("dll$flop") == 0) {
		return true;
	}
	
	if (string(pquery).find("dll$turn") == 0) {
		return true;
	}

	if (string(pquery).find("dll$river") == 0) {
		return true;
	}

	return false;
}

int do_openppl_command(const char* pquery)
{
    // return query_openppl_command(pquery);

	// call everything
	if (string(pquery).find("_call") != string::npos)
		return 1;
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
		return do_new_round();

	if (is_heartbeat(pquery)) {
		do_heartbeat();
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