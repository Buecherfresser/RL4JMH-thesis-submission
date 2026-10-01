"""Thin entrypoint: merge a trained LoRA adapter into its base model.

See :mod:`jmhgen.models.merge` for why this does a state-dict-level merge (Gemma 4 KV-sharing
drops ``k_norm``/``k_proj``/``v_proj`` for the shared layers on a naive PEFT round-trip).
"""

from jmhgen.models.merge import main

if __name__ == "__main__":
    main()
