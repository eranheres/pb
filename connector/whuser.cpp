#include <string>
#include <string.h>
#include <sstream>

#include <windows.h>
#include <process.h>
#include <vector>

#include "whuser.h"

using namespace std;

pfgws_t m_pget_winholdem_symbol = NULL;

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
void send_symbols()
{
}

/*
 * Handler for OH query message.
 */
void process_query(const char* pquery)
{
	try {
		printf("%s",pquery);
	} catch(...){}
}

/*
 * Handler for OH state message.
 */
void process_state(holdem_state* pstate)
{
}

/*
 * Handler for OH messages.
 */
WHUSER_API double process_message(const char* pmessage,	const void* param)
{
	msg(L"process message",L"message");
	if (pmessage == NULL || param == NULL)
		return 0;

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
		send_symbols();
		process_query((const char*)param);
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
	symbol_need = CreateEvent(NULL, FALSE, FALSE, NULL);
	symbol_ready = CreateEvent(NULL, FALSE, FALSE, NULL);
	have_result = CreateEvent(NULL, FALSE, FALSE, NULL);
}

/*
 * Create XMLRPC Client and Server.
 */
void DLL_START()
{
	bool dupa;
	dll_listening_port = int(m_pget_winholdem_symbol(0, "f$dll_listening_port", dupa));
	int client_listening_port = int(m_pget_winholdem_symbol(0, "f$client_listening_port", dupa));

	if(client_listening_port)
	{
		std::ostringstream os;
		os << "http://localhost:" << client_listening_port << "/RPC2";
	}

	uintptr_t th = _beginthread(xServerThread, 0, NULL);

	ClientData* cd = (ClientData*)malloc(sizeof(ClientData));
		
	cd->format = "sn";
	cd->type = "event";
	cd->data = NULL;

	_beginthread(xClientThread, 0, cd);
	handle_xClient();
}

/*
 * DLL unloaded from OH.
 */
void DLL_UNLOAD()
{
	ClientData* cd = (ClientData*)malloc(sizeof(ClientData));
		
	cd->format = "sn";
	cd->type = "event";
	cd->data = NULL;

	_beginthread(xClientThread, 0, cd);
	handle_xClient();

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
