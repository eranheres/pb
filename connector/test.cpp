#include "whuser.h"
#include "memory.h"
#include <string>
#include <cstdio>
#include "stdio.h"

extern std::string holdem_state_to_string(holdem_state*);
extern std::string holdem_state_to_json(holdem_state*);

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
    printf("%s\n", holdem_state_to_json(state).c_str());
    free(state);
    return 1;
}

void main() {
    test_holdem_state_to_string();
	test_holdem_state_to_json();
	printf("press enter to exit...\n");
	std::getchar();
}


    
