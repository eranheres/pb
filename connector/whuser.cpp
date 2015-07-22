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
#include "whuser.h"

using namespace std;

pfgws_t m_pget_winholdem_symbol = NULL;
bool LOADED = false;

HANDLE symbol_need, symbol_ready, have_result;

double client_result;

int dll_listening_port = 0;

string symbol_name;
double symbol_value;
int chair;

std::vector<std::string> subscribed_symbols;

/*
 * Show messagebox.
 */
void msg(const wchar_t* m, const wchar_t* t)
{
    int msgboxID = MessageBox(
            NULL,
            (LPCWSTR)m,
            (LPCWSTR)t,
            MB_OK);
}

string num_to_card(unsigned char num) 
{
    if (num==0xFE) return "";
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

string holdem_state_to_json(holdem_state* state) {
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

	// build the main object
    json_t *main = json_object();
	json_object_set_new(main, "title", json_string((char*)state->m_title));
	json_object_set_new(main, "is_playing", json_integer(state->m_is_playing));
	json_object_set_new(main, "is_posting", json_integer(state->m_is_posting));
	json_object_set_new(main, "fillerbits", json_integer(state->m_fillerbits));
	json_object_set_new(main, "dealer_chair", json_integer(state->m_dealer_chair));
	json_object_set_new(main, "pots", pots);
    json_object_set_new(main, "players", players);
	
    return json_dumps(main, JSON_INDENT(4));
}

void Log(const char* szString)
{
    FILE* pFile = fopen("logFile.txt", "a");
    fprintf(pFile, "%s",szString);
    fclose(pFile);
}

/*
 * Thread responsible for handling OpenHoldem symbol requests.
 */
void xServerThread(void* dummy)
{
}

struct ClientData
{
    char* format;
    char* type;
    void* data;
};

/*
 * Thread responsible for sending information to OH.
 */
void xClientThread(void* client_data)
{
}

/*
 * Send subscrived symbols to client.
 */

/*
 * Handler for OH query message.
 */
double process_query(const char* pquery)
{
    ostringstream os;
    os << "query:" << pquery << "\n";
    Log(os.str().c_str());
    return 0;
}

/*
 * Handler for OH state message.
 */
void process_state(holdem_state* pstate)
{
    ostringstream os;
    os << "state:" << holdem_state_to_string(pstate);
    //Log(os.str().c_str());
}

/*
 * Handler for OH messages.
 */
WHUSER_API double process_message(const char* pmessage,	const void* param)
{
    if (pmessage == NULL || param == NULL)
        return 0;

    ostringstream os;
    os << "message; "<< pmessage << "\n";
    Log(os.str().c_str());

    if (strcmp(pmessage, "event") == 0)
    {
        if(!LOADED)
        {
            LOADED = true;
            DLL_LOAD();
        }
        else
        {
            DLL_UNLOAD();
        }
    }

    else if (strcmp(pmessage, "state") == 0)
        process_state((holdem_state*)param);

    else if (strcmp(pmessage, "query") == 0)
    {
        double ret = process_query((const char*)param);
		ostringstream string;
		string<<"query return" << ret;
        Log(string.str().c_str());
        return ret;
    }
    else if (strcmp(pmessage, "pfgws") == 0)
    {
        m_pget_winholdem_symbol = (pfgws_t)param;
        DLL_START();
        return 0;
    }
    else if (strcmp(pmessage, "phl1k") == 0)
        return 0;

    else if (strcmp(pmessage, "prw1326") == 0)
        return 0;

    else if (strcmp(pmessage, "p_send_chat_message") == 0)
        return 0;

    return client_result;
}

void handle_xClient()
{
    DWORD wait_result;
    bool dupa;
    while(true)
    {
        wait_result = WaitForSingleObject(have_result, 100);
        if(WAIT_OBJECT_0 == wait_result)
        {
            break;
        }
        wait_result = WaitForSingleObject(symbol_need, 100);
        if(WAIT_OBJECT_0 == wait_result)
        {
            symbol_value = m_pget_winholdem_symbol(chair, symbol_name.c_str(), dupa);
            SetEvent(symbol_ready);
        }
    }
}

/*
 * DLL loaded to OH.
 */
void DLL_LOAD()
{
    Log("Loading DLL...\n");
    symbol_need = CreateEvent(NULL, FALSE, FALSE, NULL);
    symbol_ready = CreateEvent(NULL, FALSE, FALSE, NULL);
    have_result = CreateEvent(NULL, FALSE, FALSE, NULL);
    Log("DLL loaded\n");
}

/*
 * Create XMLRPC Client and Server.
 */
void DLL_START()
{
    bool dupa;
    dll_listening_port = int(m_pget_winholdem_symbol(0, "f$dll_listening_port", dupa));
    int client_listening_port = int(m_pget_winholdem_symbol(0, "f$client_listening_port", dupa));

}

/*
 * DLL unloaded from OH.
 */
void DLL_UNLOAD()
{
    Log("Unloading DLL\n");
    ClientData* cd = (ClientData*)malloc(sizeof(ClientData));
 
    cd->format = "sn";
    cd->type = "event";
    cd->data = NULL;

}

BOOL APIENTRY DllMain(HANDLE hModule, DWORD ul_reason_for_call, LPVOID lpReserved)
{
    switch (ul_reason_for_call)
    {
        case DLL_PROCESS_ATTACH:
        case DLL_THREAD_ATTACH:
        case DLL_THREAD_DETACH:
        case DLL_PROCESS_DETACH:
            break;
    }
    return TRUE;
}
