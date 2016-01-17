#include "whuser.h"
#include "memory.h"
#include <string>
#include <cstdio>
#include "stdio.h"

extern std::string holdem_state_to_string(holdem_state*);
extern std::string holdem_state_to_json_string(holdem_state*);
extern int request(const char *url, char* buffer, unsigned int buffer_size);
void init_curl();
void clean_curl();
//extern char* request(const char *url);

void build_holdem_state(holdem_state* state) {
    strcpy(&state->m_title[0], "test_name");
    for (int i=0; i<5; i++)
        state->m_cards[i] = i;
    for (int i=0; i<10; i++)
        state->m_pot[i] = i;
    state->m_is_playing = 1;
    state->m_is_posting = 1;
    state->m_fillerbits = 2;
    state->m_dealer_chair = 5;
}

int test_holdem_state_to_string() {
    holdem_state* state = (holdem_state*)malloc(sizeof(holdem_state));
	build_holdem_state(state);
    printf("%s\n", holdem_state_to_string(state).c_str());
    free(state);
    return 1;
}

int test_holdem_state_to_json() {
    holdem_state* state = (holdem_state*)malloc(sizeof(holdem_state));
	build_holdem_state(state);
    printf("%s\n", holdem_state_to_json_string(state).c_str());
    free(state);
    return 1;
}

#define URL "http://192.168.100.85:8080/tabledata"
//#define URL "http://localhost"

int test_send_json_state() {
	holdem_state* state = (holdem_state*)malloc(sizeof(holdem_state));
	build_holdem_state(state);
	const int buffer_size = (1024 * 256);
	char* buffer = (char*)malloc(buffer_size);
	strncpy(buffer,holdem_state_to_json_string(state).c_str(),buffer_size);
    request(URL, buffer, strlen(buffer));
	free(buffer);
	free(state);
	return 1;
}

void main(int argc, char* argv[]) {
	init_curl();
    test_holdem_state_to_string();
	test_holdem_state_to_json();
	test_send_json_state();
	printf("press enter to exit...\n");
	clean_curl();
	std::getchar();
}


    
