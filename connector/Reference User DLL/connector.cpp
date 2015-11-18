#define USER_DLL

#include "user.h"
#include <string>
#include <string.h>
#include <sstream>
#include "stdio.h"

#include <windows.h>
#include <process.h>
#include <vector>
#include <fstream>
#include <bitset>
#include "jansson.h"
#include "curl/curl.h"
#include "AllSymbols.h"
#include "AllPPLSymbols.h"

using namespace std;
//#define Log printf
//#define Log WriteLog
#define Log pblog
void init_curl();
void clean_curl();
int request(const char *url, char* buffer, unsigned int buffer_size);

HANDLE symbol_need, symbol_ready, have_result;

double client_result;

int hand_count = 0;
const char* hand_turn = 0;
int dll_listening_port = 0;

string symbol_name;
double symbol_value;
int chair;
static CURL* curl = NULL;
static const int BUFFER_SIZE = (1024 * 256);

#define STATE_INDEX (state_index==0?255:state_index-1)
#define CARD_VALID(card) (card<0xFE)

struct configuration_st {
	string server_url;
} configuration;

#define BUF_SIZE (256*1024)
void pblog(char* fmt,...) {
	char buffer[BUF_SIZE];
	va_list args;
    va_start(args, fmt);
	vsprintf_s(buffer, BUF_SIZE, fmt, args);
	va_end(args);
	WriteLog(buffer);
	printf(buffer);
}

/*
 * Show messagebox.
 */
void msg(const wchar_t* m, const wchar_t* t)
{
    int msgboxID = MessageBox(
            NULL,
            (LPCSTR)m,
            (LPCSTR)t,
            MB_OK);
}

string num_to_card(unsigned char num) 
{
    if (!CARD_VALID(num)) return "";
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

string holdem_player_to_string(holdem_player* player)
{
    ostringstream os;
    os  << "name:" 
        << (player->m_name_known ? (char*)player->m_name : "unkown") 
        << "|balance:" 
        << (player->m_balance_known ? (int)player->m_balance : -1)
        << "$|bet:" << player->m_currentbet 
        << "|cards:" << num_to_card(player->m_cards[0])  
        << num_to_card(player->m_cards[1])
        << "|fillerbits:"<< bitset<6>((int)player->m_fillerbits).to_string()
        << "|fillerbyte:"<< (int)player->m_fillerbyte; 
    return os.str();
}

string holdem_state_to_string(holdem_state* state)
{
    ostringstream os;
    os << (char*)state->m_title << "|board:";
    for (int i=0; i<5; i++) {
        os << num_to_card(state->m_cards[i]);
        if (i!=4) os << ",";
    }
    os << "|pot:";
    for (int i=0; i<10; i++) {
        os << (int)state->m_pot[i];
        if (i!=9) os << ",";
    }
    os << (state->m_is_playing?"|play":"|not play")
       << (state->m_is_posting?"|post":"|not post") 
       << "|fill:" << bitset<6>((int)state->m_fillerbits).to_string()
       << "|deal:" << (int)state->m_dealer_chair
       << "\n";
    for (int i=0; i<10; i++) {
        if (state->m_player[i].m_name_known == 0)
            continue;
        os << "    " << holdem_player_to_string(&state->m_player[i]) << "\n";
    }
    return os.str();
}


json_t* json_holdem_player(holdem_player* player) {
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

	return main;
}

json_t* holdem_state_to_json(holdem_state* state) {
	char *hand_number = GetHandnumber();

	// build hand matadata
	json_t *matadata = json_object();
	json_object_set_new(matadata, "room", json_string("DDPoker"));
	json_object_set_new(matadata, "table", 0);
	json_object_set_new(matadata, "hand", json_integer(hand_count));
	json_object_set_new(matadata, "turn", json_string(hand_turn));

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
		json_array_append_new(players, json_holdem_player(&state->m_player[i]));

	// add symbols
    json_t *symbols = json_object();
	const char* sym = 0;
	int idx = 0;
	while (*(sym = all_symbol_names[idx++]) != '\0') {
	    double val = GetSymbol(sym);
	    json_object_set_new(symbols, sym, json_real(val));
	}

	json_t *ppl_symbols = json_object();
	idx = 0;
	while (*(sym = all_ppl_symbol_names[idx++]) != '\0') {
		double val = GetSymbol(sym);
		json_object_set_new(symbols, sym, json_real(val));
	}
	
	// build the main object
    json_t *main = json_object();
	json_object_set_new(main, "title", json_string((char*)state->m_title));
	json_object_set_new(main, "is_playing", json_integer(state->m_is_playing));
	json_object_set_new(main, "is_posting", json_integer(state->m_is_posting));
	json_object_set_new(main, "fillerbits", json_integer(state->m_fillerbits));
	json_object_set_new(main, "dealer_chair", json_integer(state->m_dealer_chair));
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

int is_showdown() {
	int idx = state_index;
    holdem_state* cstate = &state[STATE_INDEX];
	for (int i=0; i<5; i++) {
		if (!CARD_VALID(cstate->m_cards[i]))
			return false;
	}

	holdem_player* player = 0;
	int show_count = 0;
	for (int i=0; i<10; i++) {
		player = &cstate->m_player[i];
		if ((player->m_name_known) && (CARD_VALID(player->m_cards[0])) && (CARD_VALID(player->m_cards[1])))
		    show_count++;
		if (show_count == 2)
			return true;
	}
	return false;
}

int do_showdown()
{
	Log("Showdown. handnumber:%d\n",hand_count); 
	return 0;
}

int is_openppl_command(const char* pquery) {

	if (string(pquery).find("dll$preflop_") == 0) {
		hand_turn = "preflop";
		return true;
	}

	if (string(pquery).find("dll$flop") == 0) {
		hand_turn = "flop";
		return true;
	}
	
	if (string(pquery).find("dll$turn") == 0) {
		hand_turn = "turn";
		return true;
	}

	if (string(pquery).find("dll$river") == 0) {
		hand_turn = "river";
		return true;
	}

	return false;
}

int do_openppl_command(const char* pquery)
{
	int pstate = STATE_INDEX;
	typedef holdem_state jj[256];
	jj* phstate = (jj*)&state[0];
	holdem_state* current = &state[pstate];
	
	Log("query: %s, event: hand=%d,turn=%s\n", pquery, hand_count, hand_turn);
	//Log("state:%s\n", holdem_state_to_json_string(&state[STATE_INDEX]).c_str());
    //Log("query:%s\n", pquery);

	// call everything
	if (string(pquery).find("_call") != string::npos)
		return 1;
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
	//Log("heartbeat...\n");
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

int is_hand_reset(const char* pquery) 
{
    if (string(pquery) != "dll$ini_function_on_handreset")
	    return 0;	
	Log("Handreset:%d\n",hand_count);
	hand_count++;
	return 1;
}

int do_hand_reset() 
{
	hand_count++;
	return 0;
}

int is_new_round(const char* pquery)
{
	if (string(pquery) == "dll$ini_function_on_new_round")
	{
		Log("On new round\n");
		return 1;
	}
	return 0;
}

int do_new_round()
{
    Log("state :%s\n", holdem_state_to_json_string(&state[STATE_INDEX]).c_str());
	return 0;
}

int is_my_turn(const char* pquery) 
{
    return (string(pquery) == "dll$ini_function_on_my_turn")
}

int do_my_turn(const char* pquery)
{
	Log("Do my turn\n");
}
/*
 * Handler for OH query message.
 */
double process_query(const char* pquery)
{
	if (is_hand_reset(pquery)) 
		return do_hand_reset();

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

	if (is_my_turn())
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

void read_config() 
{
	json_t *json;
	json_error_t error;

	json = json_load_file("pb_config.json", 0, &error);
	if(!json) {
		Log("failed to load config file\n");
		return;
	}
	WriteLog("test %s:\n","This is a test 123");
    Log("config file loaded\n");	
	
    json_t* url = json_object_get(json,"server_url");
	if (json_is_string(url)) {
    	configuration.server_url = json_string_value(url);
		Log("Server URL:%s\n",configuration.server_url.c_str());
	}
}

void init_connector()
{
	init_curl();
	read_config();
}

void clean_connector()
{
	clean_curl();
}
